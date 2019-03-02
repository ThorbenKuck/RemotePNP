package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class UploadAccepted implements Serializable {

	private static final long serialVersionUID = -8108909812026866400L;
	private final String id;
	private final User started;

	public UploadAccepted(String id, User started) {
		this.id = id;
		this.started = started;
	}

	public String getId() {
		return id;
	}

	public User getUser() {
		return started;
	}

	@Override
	public String toString() {
		return "UploadAccepted{" +
				"id='" + id + '\'' +
				", user=" + started +
				'}';
	}
}
