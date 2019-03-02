package com.gitub.thorbenkuck.tears.server.network;

import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.network.Session;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class ServerUserImpl implements ServerUser {

	private User user;
	private final Session session;
	private final Random random = new Random();

	ServerUserImpl(Session session) {
		this.session = session;
	}

	@Override
	public void notify(Object object) {
		try {
			Logger.debug("Notifying " + user + " about " + object);
			session.send(object);
		} catch (IllegalStateException ignored) {
		}
	}

	@Override
	public User toShared() {
		return user;
	}

	@Override
	public boolean isValid() {
		return user != null && session.isIdentified();
	}

	@Override
	public void set(User user) {
		this.user = user;
	}

	@Override
	public int randomNumber(int upTo) {
		return random.nextInt(upTo) + 1;
	}

	@Override
	public String getAddress() {
		return session.getIdentifier();
	}

	@Override
	public void identify() {
		session.setIdentified(true);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServerUserImpl that = (ServerUserImpl) o;
		return Objects.equals(user, that.user) &&
				Objects.equals(session, that.session);
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, session);
	}

	@Override
	public String toString() {
		return "ServerUserImpl{" + user + '}';
	}

	void identifySession() {
		session.setIdentified(true);
	}
}
