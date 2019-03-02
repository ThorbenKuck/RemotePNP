package com.gitub.thorbenkuck.tears.server.network;

import java.io.Serializable;

public interface Message<T> {

	T getCore();

	void answer(Serializable serializable);

	ServerUser getServerUser();
}
