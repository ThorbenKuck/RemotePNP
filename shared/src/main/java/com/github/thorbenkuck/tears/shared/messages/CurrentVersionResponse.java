package com.github.thorbenkuck.tears.shared.messages;

import java.io.Serializable;

public final class CurrentVersionResponse implements Serializable {
	private static final long serialVersionUID = -1926182682121338078L;
	private final int version;
	private final String updateMessage;

	public CurrentVersionResponse(int version, String updateMessage) {
		this.version = version;
		this.updateMessage = updateMessage;
	}

	@Override
	public String toString() {
		return "CurrentVersionResponse{" +
				"version=" + version +
				'}';
	}

	public int getVersion() {
		return version;
	}

	public String getUpdateMessage() {
		return updateMessage;
	}
}
