package com.gitub.thorbenkuck.tears.server.network;

@FunctionalInterface
public interface MessageHandler<T> {

	void handle(Message<T> message);

}
