package com.github.thorbenkuck;

import com.gitub.thorbenkuck.tears.client.network.p2p.Peer;
import com.gitub.thorbenkuck.tears.client.network.p2p.Peer2PeerConnection;

import java.io.IOException;

public class UDPClientTest {

	public static void main(String[] args) throws IOException {
		Peer2PeerConnection connection = Peer.connectUDP("localhost", 12345);
		connection.printLine("Hello");
		System.out.println(connection.readLine());
		System.out.println(connection.readInt());
		System.out.println(new String(connection.read()));
		connection.close();
	}
}
