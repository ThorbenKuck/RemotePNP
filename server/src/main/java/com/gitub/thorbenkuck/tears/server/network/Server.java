package com.gitub.thorbenkuck.tears.server.network;

import com.github.thorbenkuck.tears.shared.exceptions.ConnectionEstablishmentFailedException;
import com.github.thorbenkuck.tears.shared.pipeline.Pipeline;

public interface Server {

	static Server at(int port) throws ConnectionEstablishmentFailedException {
		return new ServerImpl(port);
	}

	Pipeline<ServerUser> getDisconnectedPipeline();

	void run();

	<T> void handle(Class<T> type, MessageHandler<T> handler);

	void verify(ServerUser serverUser);

}
