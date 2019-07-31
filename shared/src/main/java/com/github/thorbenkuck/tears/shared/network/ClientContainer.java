package com.github.thorbenkuck.tears.shared.network;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientContainer {

	private final Connection connection;
	private final CommunicationRegistration communicationRegistration = new CommunicationRegistration();
	private ObjectEncoder objectEncoder = new JavaObjectEncoder();
	private ObjectDecoder objectDecoder = new JavaObjectDecoder();

	public ClientContainer(String address, int port) throws IOException {
		Socket socket = new Socket(address, port);
		socket.setKeepAlive(true);
		socket.setReuseAddress(true);
		socket.setTcpNoDelay(true);
		connection = Connection.wrap(socket, this::convert);
		connection.outputStream().subscribe(this::handleReceive);
	}

	private byte[] convert(Object o) {
		return objectEncoder.apply(o);
	}

	private void handleReceive(RawData rawData) {
		communicationRegistration.trigger(objectDecoder.apply(rawData.getData()), connection);
	}

	public CommunicationRegistration getCommunicationRegistration() {
		return communicationRegistration;
	}

	public ObjectEncoder getObjectEncoder() {
		return objectEncoder;
	}

	public void setObjectEncoder(ObjectEncoder objectEncoder) {
		this.objectEncoder = objectEncoder;
	}

	public ObjectDecoder getObjectDecoder() {
		return objectDecoder;
	}

	public void setObjectDecoder(ObjectDecoder objectDecoder) {
		this.objectDecoder = objectDecoder;
	}

	public void send(Object o) throws IOException {
		connection.write(objectEncoder.apply(o));
	}

	public void close() {
		connection.closeSilently();
	}

	public void onDisconnect(Consumer<Connection> connectionConsumer) {
		connection.setOnDisconnect(connectionConsumer);
	}

	public void listen() {
		connection.listen();
	}
}
