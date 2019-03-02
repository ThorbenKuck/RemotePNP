package com.gitub.thorbenkuck.tears.client.network.p2p;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.*;
import java.net.Socket;

class TCPPeer2PeerConnection implements Peer2PeerConnection {

	private final Socket endpoint;
	private final DataOutputStream dataOutputStream;
	private final DataInputStream dataInputStream;

	TCPPeer2PeerConnection(Socket endpoint) throws IOException {
		this.endpoint = endpoint;

		Logger.debug("Connection established to " + endpoint.getRemoteSocketAddress());

		dataInputStream = new DataInputStream(endpoint.getInputStream());
		dataOutputStream = new DataOutputStream(endpoint.getOutputStream());
	}

	@Override
	public String readLine() throws IOException {
		return dataInputStream.readUTF();
	}

	@Override
	public int readInt() throws IOException {
		return dataInputStream.readInt();
	}

	@Override
	public void writeInt(int i) throws IOException {
		dataOutputStream.writeInt(i);
	}

	@Override
	public void write(byte[] data) throws IOException {
		Logger.debug("");
		dataOutputStream.writeInt(data.length);
		dataOutputStream.write(data);
		dataOutputStream.flush();
	}

	@Override
	public void printLine(String line) throws IOException {
		dataOutputStream.writeUTF(line);
		dataOutputStream.flush();
	}

	@Override
	public void printLine() throws IOException {
		dataOutputStream.writeUTF("");
		dataOutputStream.flush();
	}

	@Override
	public byte[] read() throws IOException {
		int length = dataInputStream.readInt();
		Logger.debug("Message length: " + length);
		if (length > 0) {
			byte[] message = new byte[length];
			dataInputStream.readFully(message, 0, length);
			return message;
		} else {
			throw new IOException("Message expected is les than 0!");
		}
	}

	@Override
	public void close() {
		Logger.debug("Closing Peer2Peer connection");
		try {
			endpoint.close();
		} catch (IOException e) {
			Logger.trace("Exception while closing endpoint " + e.getMessage());
		}
		try {
			dataInputStream.close();
		} catch (IOException e) {
			Logger.trace("Exception while closing inputStream " + e.getMessage());
		}
		try {
			dataOutputStream.close();
		} catch (IOException e) {
			Logger.trace("Exception while closing outputStream " + e.getMessage());
		}
	}

	@Override
	public boolean isOpen() {
		return endpoint.isConnected();
	}
}
