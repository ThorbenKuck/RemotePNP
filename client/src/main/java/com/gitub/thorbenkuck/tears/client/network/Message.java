package com.gitub.thorbenkuck.tears.client.network;

import java.io.Serializable;

public interface Message<T> {

	T getCore();

	void answer(Serializable serializable);

}
