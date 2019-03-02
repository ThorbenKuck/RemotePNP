package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class JoinServerResponse implements Serializable {

	private final User user;
	private final String message;
	private static final long serialVersionUID = 1533782966198968774L;

	public JoinServerResponse(String message) {
		this.user = null;
		this.message = message;
	}

	public JoinServerResponse(User user) {
		this.user = user;
		this.message = "Ok";
	}

	public boolean isSuccess() {
		return user != null;
	}

	public User getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "JoinServerResponse{" +
				"user=" + user +
				", message='" + message + '\'' +
				'}';
	}
}
