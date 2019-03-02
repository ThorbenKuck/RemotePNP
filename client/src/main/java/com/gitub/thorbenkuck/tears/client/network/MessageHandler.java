package com.gitub.thorbenkuck.tears.client.network;

@FunctionalInterface
public interface MessageHandler<T> {

	void handle(Message<T> message);

}
