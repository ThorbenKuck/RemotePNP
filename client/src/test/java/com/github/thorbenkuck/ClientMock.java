package com.github.thorbenkuck;

import com.github.thorbenkuck.tears.shared.exceptions.ConnectionEstablishmentFailedException;
import com.github.thorbenkuck.tears.shared.network.Connection;
import com.github.thorbenkuck.tears.shared.pipeline.Pipeline;
import com.gitub.thorbenkuck.tears.client.network.Client;
import com.gitub.thorbenkuck.tears.client.network.MessageHandler;
import com.google.common.eventbus.EventBus;

import java.io.Serializable;

public class ClientMock implements Client {
	@Override
	public Pipeline<Connection> disconnectedPipeline() {
		return new Pipeline<>();
	}

	@Override
	public void launch() throws ConnectionEstablishmentFailedException {

	}

	@Override
	public void send(Serializable serializable) {

	}

	@Override
	public void registerTo(EventBus eventBus) {

	}

	@Override
	public <T> void handleSpecific(Class<T> type, MessageHandler<T> messageHandler) {

	}

	@Override
	public String getAddress() {
		return null;
	}

	@Override
	public void close() {

	}
}
