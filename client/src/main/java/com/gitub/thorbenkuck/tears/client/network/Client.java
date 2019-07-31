package com.gitub.thorbenkuck.tears.client.network;

import com.github.thorbenkuck.tears.shared.exceptions.ConnectionEstablishmentFailedException;
import com.github.thorbenkuck.tears.shared.network.Connection;
import com.github.thorbenkuck.tears.shared.pipeline.Pipeline;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.google.common.eventbus.EventBus;

import java.io.Serializable;

public interface Client {

	static Client create(String address, int port, Repository repository) {
		Client client =  new ClientImpl(address, port);
		ClientSetup.setup(client, repository);

		return client;
	}

	Pipeline<Connection> disconnectedPipeline();

	void launch() throws ConnectionEstablishmentFailedException;

	void send(Serializable serializable);

	void registerTo(EventBus eventBus);

	<T> void handleSpecific(Class<T> type, MessageHandler<T> messageHandler);

	String getAddress();

	void close();
}
