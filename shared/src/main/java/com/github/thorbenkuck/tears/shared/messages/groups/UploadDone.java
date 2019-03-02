package com.github.thorbenkuck.tears.shared.messages.groups;

import java.io.Serializable;

public final class UploadDone implements Serializable {

	private static final long serialVersionUID = 8963347628997405059L;
	private final String id;

	public UploadDone(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "UploadDone{" +
				"id='" + id + '\'' +
				'}';
	}
}
