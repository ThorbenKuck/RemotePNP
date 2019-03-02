package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class JoinServerRequest implements Serializable {

	private final User user;
	private static final long serialVersionUID = 4413000946009162600L;

	/**
	 * You have to provide the user, you want to join with.
	 *
	 * The idea is, that the user object may be stored locally and reused once you want to keep playing
	 *
	 * @param user the user you want to use
	 */
	public JoinServerRequest(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "JoinServerRequest{" +
				"user=" + user +
				'}';
	}
}
