package com.gitub.thorbenkuck.tears.client.network.p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class UDPPeer2PeerConnection implements Peer2PeerConnection {

	private final DatagramSocket socket;
	private final SocketAddress socketAddress;

	public UDPPeer2PeerConnection(DatagramSocket socket, SocketAddress address) {
		this.socket = socket;
		socketAddress = address;
	}

	private void send(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length);
		packet.setSocketAddress(socketAddress);
		socket.send(packet);
	}

	private byte[] awaitNextPacket(byte[] buffer) throws IOException {
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		socket.receive(datagramPacket);
		return datagramPacket.getData();
	}

	/**
	 *           Other               Me
	 *           _____               ___
	 *             | writeInt(length) |
	 *             |-----------------x|--|
	 *             | writeInt(read)   |  | readInt()
	 *             |x-----------------|x-|
	 *          |--|				  |
	 *readInt() |  |				  |
	 *check()   |  |	send(string)  |
	 *          |-x|-----------------x|--|
	 *             |				  |  | receive(length)
	 *             |				  |x-|
	 *             |				  |
	 *
	 * @return the next line
	 * @throws IOException if something goes wrong
	 */
	@Override
	public String readLine() throws IOException {
		int length = readInt();
		writeInt(length);
		byte[] data = awaitNextPacket(new byte[length]);
		return new String(data);
	}

	@Override
	public void printLine(String line) throws IOException {
		byte[] data = line.getBytes();
		int length = data.length;
		writeInt(length);
		int remoteLength = readInt();
		if (remoteLength != length) {
			throw new IOException("Remote server allocated to little memory (expected=" + length + ", actual=" + remoteLength + ")");
		}
		send(data);
	}

	@Override
	public void printLine() throws IOException {
		printLine("");
	}

	@Override
	public int readInt() throws IOException {
		return ByteBuffer.wrap(awaitNextPacket(new byte[4])).getInt();
	}

	@Override
	public void writeInt(int i) throws IOException {
		byte[] array = ByteBuffer.allocate(4).putInt(i).array();
		send(array);
	}

	@Override
	public void write(byte[] data) throws IOException {
		writeInt(data.length);
		send(data);
	}

	@Override
	public byte[] read() throws IOException {
		int length = readInt();
		return awaitNextPacket(new byte[length]);
	}

	@Override
	public void close() {
		socket.close();
	}

	@Override
	public boolean isOpen() {
		return socket.isConnected() && socket.isConnected();
	}
}
