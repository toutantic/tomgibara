package com.tomgibara.crinch.record.compact;

import java.math.BigDecimal;
import java.util.Arrays;

import com.tomgibara.crinch.bits.BitWriter;
import com.tomgibara.crinch.coding.CodedReader;
import com.tomgibara.crinch.coding.CodedWriter;
import com.tomgibara.crinch.coding.HuffmanCoding;
import com.tomgibara.crinch.record.ColumnStats;
import com.tomgibara.crinch.record.ColumnStats.Classification;

class ColumnCompactor {

	private static int[][] rank(long[] fs) {
		int len = fs.length;
		El[] els = new El[len];
		for (int i = 0; i < len; i++) {
			els[i] = new El(i, fs[i]);
		}
		Arrays.sort(els);
		int lim;
		for (lim = 0; lim < len; lim++) {
			//if (els[lim].freq == 0) break;
		}
		
		int[][] rs = new int[2][lim];
		for (int i = 0; i < lim; i++) {
			El el = els[i];
			fs[i] = el.freq;
			int j = el.index;
			rs[0][i] = j;
			rs[1][j] = i;
		}
		return rs;
	}
	
	private static class El implements Comparable<El> {
		
		int index;
		long freq;
		
		El(int index, long freq) {
			this.index = index;
			this.freq = freq;
		}
		
		@Override
		public int compareTo(El that) {
			if (this.freq == that.freq) return 0;
			return this.freq < that.freq ? 1 : -1;
		}
		
	}
	
	private final ColumnStats stats;
	// derived from stats
	private final boolean nullable;
	private final long offset;
	private final String[] enumeration;
	
	private final int[][] lookup;
	private final HuffmanCoding huffman;
	
	ColumnCompactor(ColumnStats stats) {
		this.stats = stats;
		this.nullable = stats.isNullable();
		this.enumeration = stats.getClassification() == Classification.ENUMERATED ? stats.getEnumeration() : null;
		switch (stats.getClassification()) {
		case INTEGRAL:
		case TEXTUAL:
			this.offset = stats.getSum().divideToIntegralValue(BigDecimal.valueOf(stats.getCount())).longValue();
			break;
			default:
				this.offset = 0L;
		}
		long[] freqs = stats.getFrequencies();
		if (freqs == null) {
			lookup = null;
			huffman = null;
		} else {
			long[] fs = freqs.clone();
			lookup = rank(fs);
			for (int i = 0; i < fs.length; i++) {
				if (fs[i] == 0L) {
					fs = Arrays.copyOfRange(fs, 0, i);
					break;
				}
			}
			//System.out.println("*** " + Arrays.toString(fs));
			//fs = Arrays.copyOfRange(fs, 0, lookup[0].length);
			huffman = new HuffmanCoding(fs);
		}
	}

	ColumnStats getStats() {
		return stats;
	}
	
	int encodeNull(CodedWriter writer, boolean isNull) {
		if (!nullable) return 0;
		return writer.getWriter().writeBoolean(isNull);
	}
	
	boolean decodeNull(CodedReader reader) {
		if (!nullable) return false;
		return reader.getReader().readBoolean();
	}
	
	int encodeString(CodedWriter writer, String value) {
		BitWriter w = writer.getWriter();
		if (enumeration != null) {
			int i = Arrays.binarySearch(enumeration, value);
			if (i < 0) throw new IllegalArgumentException("Not enumerated: " + value);
			return huffman.encodePositiveInt(w, lookup[1][i]+1);
		} else {
			int length = value.length();
			int n = writer.writeSignedInt(length - (int) offset);
			for (int i = 0; i < length; i++) {
				char c = value.charAt(i);
				n += huffman.encodePositiveInt(w, lookup[1][c]+1);
			}
			return n;
		}
	}
	
	String decodeString(CodedReader reader) {
		if (enumeration != null) {
			return enumeration[ lookup[0][huffman.decodePositiveInt(reader.getReader())-1] ];
		} else {
			int length = ((int) offset) + reader.readSignedInt();
			StringBuilder sb = new StringBuilder(length);
			for (; length > 0; length--) {
				sb.append( (char) lookup[0][huffman.decodePositiveInt(reader.getReader())-1] );
			}
			return sb.toString();
		}
	}
	
	int encodeInt(CodedWriter writer, int value) {
		return writer.writeSignedInt(value - (int) offset);
	}
	
	int decodeInt(CodedReader reader) {
		return ((int) offset) + reader.readSignedInt();
	}
	
	int encodeLong(CodedWriter writer, long value) {
		return writer.writeSignedLong(value - offset);
	}
	
	long decodeLong(CodedReader reader) {
		return offset + reader.readSignedLong();
	}
	
	int encodeBoolean(CodedWriter writer, boolean value) {
		return writer.getWriter().writeBoolean(value);
	}
	
	boolean decodeBoolean(CodedReader reader) {
		return reader.getReader().readBoolean();
	}
	
	int encodeFloat(CodedWriter writer, float value) {
		//TODO support float methods directly when made available
		return writer.writeDouble(value);
	}
	
	float decodeFloat(CodedReader reader) {
		//TODO support float methods directly when made available
		return (float) reader.readDouble();
	}
	
	int encodeDouble(CodedWriter writer, double value) {
		return writer.writeDouble(value);
	}
	
	double decodeDouble(CodedReader reader) {
		return reader.readDouble();
	}

	@Override
	public String toString() {
		return stats.toString();
	}
}
