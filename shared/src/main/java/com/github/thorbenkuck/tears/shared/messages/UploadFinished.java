package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public class UploadFinished implements Serializable {

	private static final long serialVersionUID = 5572357004868887266L;
	private final String id;
	private final User user;

	public UploadFinished(String id, User user) {
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
		return "UploadFinished{" +
				"id='" + id + '\'' +
				", user=" + user +
				'}';
	}
}
