/*
 * Copyright 2007 Tom Gibara
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.tomgibara.cluster.gvm;

/**
 * A snapshot of a cluster that has been produced as the result of clustering a
 * number of coordinates.
 * 
 * @author Tom Gibara
 * 
 * @param <K>
 *            the key type
 */

public class GvmResult<K> {

	/**
	 * The number of points in the cluster.
	 */
	
	private int count;
	
	/**
	 * The aggregate mass of the cluster.
	 */
	
	private double mass;
	
	/**
	 * The coordinates of the cluster's centroid.
	 */
	
	private Object point;
	
	/**
	 * The variance of the cluster.
	 */
	
	private double variance;
	
	/**
	 * The key associated with the cluster.
	 */
	
	private K key;
	//TODO consider adding key mass

	/**
	 * Creates an empty result object
	 */
	
	public GvmResult() {
	}
	
	GvmResult(GvmCluster<?,K> cluster) {
		count = cluster.count;
		mass = cluster.m0;
		variance = cluster.var / mass;
		key = cluster.key;
		
		GvmSpace space = cluster.clusters.space;
		point = space.newCopy(cluster.m1);
		space.scale(point, 1.0 / mass);
	}

	/**
	 * The number of points in the cluster.
	 */
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * The aggregate mass of the cluster.
	 */
	
	public double getMass() {
		return mass;
	}
	
	/**
	 * Sets the aggregate mass of the cluster.
	 */
	
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	/**
	 * The coordinates of the cluster's centroid. The returned array should not
	 * be modified.
	 */

	public Object getPoint() {
		return point;
	}
	
	/**
	 * Sets the coordinates of the cluster's centroid. The values of the
	 * supplied point are copied.
	 */

	public void setPoint(Object point) {
		this.point = point;
	}

	/**
	 * The variance of the cluster.
	 */
	
	public double getVariance() {
		return variance;
	}
	
	/**
	 * Sets the variance of the cluster.
	 */
	
	public void setVariance(double variance) {
		this.variance = variance;
	}
	
	/**
	 * The key associated with the cluster.
	 */
	
	public K getKey() {
		return key;
	}
	
	/**
	 * Sets the key associated with the cluster.
	 */
	
	public void setKey(K key) {
		this.key = key;
	}
	
	// object methods
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("point: %s  count: %d  variance: %3.3f  mass: %3.3f  key: %s", point, count, variance, mass, key));
		return sb.toString();
	}
}
