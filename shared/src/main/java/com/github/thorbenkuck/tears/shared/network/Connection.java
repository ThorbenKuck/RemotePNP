package com.github.thorbenkuck.tears.shared.network;

import com.github.thorbenkuck.tears.shared.stream.EventStream;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Connection {

	static Connection wrap(Socket socket, Session session) throws IOException {
		TCPConnection tcpConnection = new TCPConnection(socket);
		tcpConnection.setSession(session);

		return tcpConnection;
	}

	static Connection wrap(Socket socket, Function<Object, byte[]> converter) throws IOException {
		TCPConnection tcpConnection = new TCPConnection(socket);
		Session session = new NativeSession(tcpConnection, converter);
		tcpConnection.setSession(session);

		return tcpConnection;
	}

	void setSession(Session session);

	void listen();

	void write(byte[] data) throws IOException;

	EventStream<RawData> outputStream();

	void closeSilently();

	void close() throws IOException;

	Session session();

	String remoteAddress();

	Consumer<Connection> getOnDisconnect();

	void setOnDisconnect(Consumer<Connection> onDisconnect);
}
