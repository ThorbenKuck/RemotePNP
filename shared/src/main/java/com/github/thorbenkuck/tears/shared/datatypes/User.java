package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;
import java.util.Objects;

public final class User implements Serializable {

	private final String userName;
	private final Character character;
	private static final long serialVersionUID = 823238037202331379L;

	public User(String userName, Character character) {
		this.userName = userName;
		this.character = character;
	}

	public String getUserName() {
		return userName;
	}

	public Character getCharacter() {
		return character;
	}

	@Override
	public String toString() {
		return userName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(userName, user.userName);
	}

	public String detailedString() {
		return "User{" +
				"username=" + userName +
				"character=" + character.detailedString()
				+ "}";
	}

	@Override
	public int hashCode() {
		return Objects.hash(userName);
	}
}
