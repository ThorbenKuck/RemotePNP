package com.gitub.thorbenkuck.tears.server.network;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.util.ArrayList;

class NullServerUser implements ServerUser {

	private User user = new User("null", new Character("null", new ArrayList<>(), new ArrayList<>(), -1, -1));

	@Override
	public void notify(Object object) {
		System.err.println("Ignoring " + object);
	}

	@Override
	public User toShared() {
		return user;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public void set(User user) {
		this.user = user;
	}

	@Override
	public int randomNumber(int upTo) {
		return 0;
	}

	@Override
	public String getAddress() {
		return "";
	}

	@Override
	public void identify() {

	}
}
