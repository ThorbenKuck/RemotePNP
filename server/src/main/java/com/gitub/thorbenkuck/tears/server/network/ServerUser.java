package com.gitub.thorbenkuck.tears.server.network;

import com.github.thorbenkuck.tears.shared.datatypes.User;

public interface ServerUser {

	static ServerUser nullObject() {
		return new NullServerUser();
	}

	void notify(Object object);

	User toShared();

	boolean isValid();

	void set(User user);

	int randomNumber(int upTo);

	String getAddress();

	void identify();
}
