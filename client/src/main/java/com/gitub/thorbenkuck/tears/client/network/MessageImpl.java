package com.gitub.thorbenkuck.tears.client.network;

import com.github.thorbenkuck.tears.shared.network.Session;

import java.io.Serializable;

class MessageImpl<T> implements Message<T> {

	private final T object;
	private final Session session;

	MessageImpl(T object, Session session) {
		this.object = object;
		this.session = session;
	}

	@Override
	public T getCore() {
		return object;
	}

	@Override
	public void answer(Serializable serializable) {
		session.send(serializable);
	}
}
