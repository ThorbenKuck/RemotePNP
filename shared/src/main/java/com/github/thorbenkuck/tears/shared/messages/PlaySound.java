package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class PlaySound implements Serializable {

	private static final long serialVersionUID = -8974827501054730014L;
	private final String soundName;
	private final User user;

	public PlaySound(String soundName) {
		this(soundName, null);
	}

	@Override
	public String toString() {
		return "PlaySound{" +
				"soundName='" + soundName + '\'' +
				", user=" + user +
				'}';
	}

	public PlaySound(String soundName, User user) {
		this.soundName = soundName;
		this.user = user;
	}

	public String getSoundName() {
		return soundName;
	}

	public User getUser() {
		return user;
	}
}
