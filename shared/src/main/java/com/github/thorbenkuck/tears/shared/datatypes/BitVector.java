package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;
import java.util.Arrays;

public final class BitVector implements Serializable {

	private static final long serialVersionUID = -1228562930315102372L;
	private boolean[] vector;

	public BitVector(int size) {
		vector = new boolean[size];
	}

	public BitVector(BitVector that) {
		this.vector = that.vector;
	}

	public BitVector(boolean... array) {
		this.vector = array;
	}

	public boolean get(int index) {
		return vector[index];
	}

	public void set(int index, boolean value) {
		vector[index] = value;
	}

	public int size() {
		return vector.length;
	}

	@Override
	public String toString() {
		return "BitVector{" + Arrays.toString(vector) + '}';
	}

	public void set(BitVector bitVector) {
		this.vector = bitVector.vector;
	}
}
