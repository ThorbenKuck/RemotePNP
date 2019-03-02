package com.github.thorbenkuck.tears.shared.network;

public class RawData {

	private final byte[] data;

	public RawData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
}
