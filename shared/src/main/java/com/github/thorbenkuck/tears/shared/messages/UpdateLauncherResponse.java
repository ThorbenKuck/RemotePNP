package com.github.thorbenkuck.tears.shared.messages;

import java.io.Serializable;

public final class UpdateLauncherResponse implements Serializable {
	private static final long serialVersionUID = -7324719958038360104L;
	private final byte[] data;

	public UpdateLauncherResponse(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "UpdateLauncherResponse{" +
				"data=" + data.length +
				'}';
	}

	public byte[] getData() {
		return data;
	}
}
