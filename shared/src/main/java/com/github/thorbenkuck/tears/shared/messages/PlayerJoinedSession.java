package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class PlayerJoinedSession implements Serializable {

	private static final long serialVersionUID = 8426110699905528437L;
	private final User user;

	public PlayerJoinedSession(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "PlayerJoinedSession{" +
				"user=" + user +
				'}';
	}
}
