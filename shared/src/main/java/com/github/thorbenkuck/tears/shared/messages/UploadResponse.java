package com.github.thorbenkuck.tears.shared.messages;

import java.io.Serializable;

public final class UploadResponse implements Serializable {

	private static final long serialVersionUID = -6062589877484692921L;
	private final String id;
	private final boolean accepted;

	public UploadResponse(String id, boolean accepted) {
		this.id = id;
		this.accepted = accepted;
	}

	public String getId() {
		return id;
	}

	public boolean isAccepted() {
		return accepted;
	}

	@Override
	public String toString() {
		return "UploadResponse{" +
				"id='" + id + '\'' +
				", accepted=" + accepted +
				'}';
	}
}
