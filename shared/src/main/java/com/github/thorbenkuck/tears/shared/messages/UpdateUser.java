package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class UpdateUser implements Serializable {

	private static final long serialVersionUID = 7749083928027583099L;
	private final User user;

	public UpdateUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "UpdateUser{" +
				"user=" + user.detailedString() +
				'}';
	}
}
