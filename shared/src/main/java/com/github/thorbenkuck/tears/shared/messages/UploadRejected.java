package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class UploadRejected implements Serializable {

	private static final long serialVersionUID = 1080082857643322871L;
	private final String id;
	private final User user;

	public UploadRejected(String id, User user) {
		this.id = id;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "UploadRejected{" +
				"id='" + id + '\'' +
				", user=" + user +
				'}';
	}
}
