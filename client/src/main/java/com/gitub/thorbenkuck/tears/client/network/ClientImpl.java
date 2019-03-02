package com.gitub.thorbenkuck.tears.client.network;

import com.github.thorbenkuck.tears.shared.exceptions.ConnectionEstablishmentFailedException;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.network.ClientContainer;
import com.github.thorbenkuck.tears.shared.network.Connection;
import com.github.thorbenkuck.tears.shared.pipeline.Pipeline;
import com.google.common.eventbus.EventBus;

import java.io.IOException;
import java.io.Serializable;

class ClientImpl implements Client {

	private final ClientContainer clientStart;
	private final Pipeline<Connection> disconnectedPipeline = new Pipeline<>();
	private final String address;

	ClientImpl(String address, int port) {
		this.address = address;
		try {
			clientStart = new ClientContainer(address, port);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		clientStart.onDisconnect(disconnectedPipeline::run);
		clientStart.getCommunicationRegistration()
				.register(String.class, (connection, s) -> System.out.println(s));
	}

	@Override
	public Pipeline<Connection> disconnectedPipeline() {
		return disconnectedPipeline;
	}

	@Override
	public void launch() throws ConnectionEstablishmentFailedException {
		try {
			clientStart.listen();
		} catch (Exception e) {
			throw new ConnectionEstablishmentFailedException(e);
		}
	}

	@Override
	public void send(Serializable serializable) {
		Logger.debug("Sending to Server: " + serializable);
		try {
			clientStart.send(serializable);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void registerTo(EventBus eventBus) {
		clientStart.getCommunicationRegistration()
				.setDefaultHandler((connection, t) -> {
					Logger.info("Received " + t);
					eventBus.post(t);
				});
	}

	@Override
	public <T> void handleSpecific(Class<T> type, MessageHandler<T> messageHandler) {
		clientStart.getCommunicationRegistration()
				.register(type, (connection, t) -> {
					Logger.info("Received " + t);
					Message<T> raw = new MessageImpl<>(t, connection.session());
					messageHandler.handle(raw);
				});
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void close() {
		clientStart.close();
	}
}
