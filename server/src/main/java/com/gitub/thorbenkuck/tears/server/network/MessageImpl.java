package com.gitub.thorbenkuck.tears.server.network;

import java.io.Serializable;

class MessageImpl<T> implements Message<T> {

	private final T object;
	private final ServerUser serverUser;

	MessageImpl(T object, ServerUser serverUser) {
		this.object = object;
		this.serverUser = serverUser;
	}

	@Override
	public T getCore() {
		return object;
	}

	@Override
	public void answer(Serializable serializable) {
		serverUser.notify(serializable);
	}

	@Override
	public ServerUser getServerUser() {
		return serverUser;
	}
}
