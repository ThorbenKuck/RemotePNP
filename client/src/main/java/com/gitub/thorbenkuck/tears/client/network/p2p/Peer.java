package com.gitub.thorbenkuck.tears.client.network.p2p;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class Peer {

	private static final byte[] UPD_HANDSHAKE_BYTES = new byte[10];

	private static Socket fromServerSocket(int port) throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			return serverSocket.accept();
		}
	}

	private static ServerSocket findOpenPort(int[] ports) throws IOException {
		for (int port : ports) {
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				serverSocket.setReuseAddress(true);
				serverSocket.bind(new InetSocketAddress("localhost", port));
			} catch (IOException ignored) {
			}
		}

		throw new IOException("No free port found in: " + Arrays.toString(ports));
	}

	private static Socket fromServerSocket(int[] ports, Consumer<Integer> preAwaitCallback) throws IOException {
		ServerSocket serverSocket = findOpenPort(ports);
		try {
			if(!serverSocket.isBound() || serverSocket.isClosed()) {
				Logger.warn("Unbound ServerSocket!");
			}
			preAwaitCallback.accept(serverSocket.getLocalPort());
			Logger.debug("Awaiting connection at port: " + serverSocket.getLocalPort());
			return serverSocket.accept();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				Logger.trace("IOException " + e.getMessage());
			}
		}
	}

	private static boolean isZero(byte[] data) {
		for (byte b : data) {
			if (b != 0) {
				return false;
			}
		}

		return true;
	}

	public static Peer2PeerConnection awaitUDP(int port) throws IOException {
		DatagramSocket datagramSocket = new DatagramSocket(port);
		Logger.debug("Established port connection at " + port);
		boolean running = true;
		DatagramPacket datagramPacket = null;
		while (running) {
			Logger.debug("Awaiting next handshake array ");
			datagramPacket = new DatagramPacket(new byte[10], UPD_HANDSHAKE_BYTES.length);
			datagramSocket.receive(datagramPacket);
			Logger.debug("received " + Arrays.toString(datagramPacket.getData()));
			if (isZero(datagramPacket.getData())) {
				Logger.debug("Handshake valid. Proceeding...");
				running = false;
				datagramSocket.send(datagramPacket);
			}
		}

		Logger.debug("Connecting DatagramSocket");
		datagramSocket.connect(datagramPacket.getSocketAddress());
		return new UDPPeer2PeerConnection(datagramSocket, datagramPacket.getSocketAddress());
	}

	public static Peer2PeerConnection connectUDP(String address, int port) throws IOException {
		DatagramSocket datagramSocket = new DatagramSocket();
		Logger.debug("Created DatagramSocket");
		DatagramPacket toSend = new DatagramPacket(UPD_HANDSHAKE_BYTES, UPD_HANDSHAKE_BYTES.length);
		toSend.setSocketAddress(new InetSocketAddress(address, port));
		Logger.debug("Sending handshake");
		datagramSocket.send(toSend);
		boolean running = true;
		DatagramPacket datagramPacket = null;
		while (running) {
			Logger.debug("Awaiting next handshake array " + port);
			datagramPacket = new DatagramPacket(new byte[10], UPD_HANDSHAKE_BYTES.length);
			datagramSocket.receive(datagramPacket);
			Logger.debug("received " + Arrays.toString(datagramPacket.getData()));
			if (isZero(datagramPacket.getData())) {
				Logger.debug("Handshake valid. Proceeding...");
				running = false;
			}
		}

		Logger.debug("Connecting DatagramSocket");
		datagramSocket.connect(datagramPacket.getSocketAddress());
		return new UDPPeer2PeerConnection(datagramSocket, datagramPacket.getSocketAddress());
	}

	public static Peer2PeerConnection await(int port) throws IOException {
		return new TCPPeer2PeerConnection(fromServerSocket(port));
	}

	public static Peer2PeerConnection await(int[] ports, Consumer<Integer> preAwaitCallback) throws IOException {
		return new TCPPeer2PeerConnection(fromServerSocket(ports, preAwaitCallback));
	}

	public static Peer2PeerConnection connect(String address, int port) throws IOException {
		Logger.debug("Trying to connect " + address + ":" + port);
		Socket socket = new Socket(address, port);
		return new TCPPeer2PeerConnection(socket);
	}
}
