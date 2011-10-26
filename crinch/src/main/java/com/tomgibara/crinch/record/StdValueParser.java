package com.tomgibara.crinch.record;

import java.util.HashSet;
import java.util.Set;

import com.tomgibara.crinch.bits.BitVector;

public class StdValueParser implements ValueParser {

	private final static String sFalseChars = "0fn";
	private final static String sTrueChars = "1ty";
	private final static String[] sFalseStrs = { "false", "no" };
	private final static String[] sTrueStrs = { "true", "yes" };
	private final static BitVector sTrueCharLookup = new BitVector(128);
	private final static BitVector sCharLookup = new BitVector(128);
	private final static Set<String> sTrueStringLookup = new HashSet<String>();
	private final static Set<String> sStringLookup = new HashSet<String>();
	
	static {
		for (char c : sTrueChars.toCharArray()) {
			sTrueCharLookup.setBit(c, true);
			sTrueCharLookup.setBit(Character.toUpperCase(c), true);
		}
		sCharLookup.setVector(sTrueCharLookup);
		for (char c : sFalseChars.toCharArray()) {
			sCharLookup.setBit(c, true);
			sCharLookup.setBit(Character.toUpperCase(c), true);
		}
		
		for (String str : sTrueStrs) {
			sTrueStringLookup.add(str);
			sTrueStringLookup.add(str.toUpperCase());
		}
		sStringLookup.addAll(sTrueStringLookup);
		for (String str : sFalseStrs) {
			sStringLookup.add(str);
			sStringLookup.add(str.toUpperCase());
		}
	}

	@Override
	public String parseString(String str) {
		return str;
	}
	
	@Override
	public char parseChar(String str) {
		if (str.isEmpty()) throw new IllegalArgumentException();
		return str.charAt(0);
	}
	
	@Override
	public boolean parseBoolean(String str) {
		int length = str.length();
		switch(length) {
		case 0: throw new IllegalArgumentException();
		case 1:
			int position = str.charAt(0);
			if (!sCharLookup.getBit(position)) throw new IllegalArgumentException();
			return sTrueCharLookup.getBit(position);
		default:
			if (!sStringLookup.contains(str)) throw new IllegalArgumentException();
			return sTrueStringLookup.contains(str);
		}
	}
	
	@Override
	public byte parseByte(String str) {
		return Byte.parseByte(str);
	}
	
	@Override
	public short parseShort(String str) {
		return Short.parseShort(str);
	}
	
	@Override
	public int parseInt(String str) {
		return Integer.parseInt(str);
	}
	
	@Override
	public long parseLong(String str) {
		return Long.parseLong(str);
	}
	
	@Override
	public float parseFloat(String str) {
		return Float.parseFloat(str);
	}
	
	@Override
	public double parseDouble(String str) {
		return Double.parseDouble(str);
	}
	
}