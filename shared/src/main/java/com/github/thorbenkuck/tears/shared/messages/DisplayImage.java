package com.github.thorbenkuck.tears.shared.messages;

import java.io.Serializable;
import java.util.Arrays;

public final class DisplayImage implements Serializable {

	private final byte[] data;
	private static final long serialVersionUID = 4282953629176371831L;

	public DisplayImage(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public String toString() {
		return "DisplayImage{" +
				"data=" + data.length +
				'}';
	}
}
