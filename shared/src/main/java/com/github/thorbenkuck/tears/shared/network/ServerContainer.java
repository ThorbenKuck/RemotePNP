package com.github.thorbenkuck.tears.shared.network;

import com.github.thorbenkuck.tears.shared.stream.EventStream;
import com.github.thorbenkuck.tears.shared.stream.SimpleEventStream;
import com.github.thorbenkuck.tears.shared.stream.WriteableEventStream;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerContainer {

	private final WriteableEventStream<Connection> connected = new SimpleEventStream<>();
	private final ServerSocket serverSocket;
	private final AtomicBoolean accepting = new AtomicBoolean(false);
	private final CommunicationRegistration communicationRegistration = new CommunicationRegistration();
	private ObjectEncoder objectEncoder = new JavaObjectEncoder();
	private ObjectDecoder objectDecoder = new JavaObjectDecoder();

	public ServerContainer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setReuseAddress(true);
		connected.subscribe(this::newConnection);
	}

	private byte[] convert(Object object) {
		return objectEncoder.apply(object);
	}

	private void newConnection(Connection connection) {
		connection.outputStream().subscribe(rawData -> receivedData(rawData, connection));
		connection.listen();
	}

	private void receivedData(RawData rawData, Connection connection) {
		communicationRegistration.trigger(objectDecoder.apply(rawData.getData()), connection);
	}

	public void accept() {
		accepting.set(true);
		while (accepting.get()) {
			try {
				Socket socket = serverSocket.accept();
				socket.setKeepAlive(true);
				socket.setReuseAddress(true);
				socket.setTcpNoDelay(true);
				connected.push(Connection.wrap(socket, this::convert));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public EventStream<Connection> ingoingConnections() {
		return connected;
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
}
