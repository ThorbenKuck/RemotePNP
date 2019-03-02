package com.gitub.thorbenkuck.tears.client.network.p2p;

import java.io.IOException;

public interface Peer2PeerConnection {

	String readLine() throws IOException;

	int readInt() throws IOException;

	void writeInt(int i) throws IOException;

	void printLine(String line) throws IOException;

	void printLine() throws IOException;

	void write(byte[] data) throws IOException;

	byte[] read() throws IOException;

	void close();

	boolean isOpen();
}
