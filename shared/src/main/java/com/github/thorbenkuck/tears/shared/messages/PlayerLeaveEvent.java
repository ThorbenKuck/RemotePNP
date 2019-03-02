package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class PlayerLeaveEvent implements Serializable {

	private static final long serialVersionUID = 3608113335721258419L;
	private final User user;

	public PlayerLeaveEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "PlayerLeaveEvent{" +
				"user=" + user +
				'}';
	}
}
