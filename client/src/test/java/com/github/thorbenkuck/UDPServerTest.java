package com.github.thorbenkuck;

import com.gitub.thorbenkuck.tears.client.network.p2p.Peer;
import com.gitub.thorbenkuck.tears.client.network.p2p.Peer2PeerConnection;

import java.io.IOException;

public class UDPServerTest {

	public static void main(String[] args) throws IOException {
		Peer2PeerConnection connection = Peer.awaitUDP(12345);
		String msg = connection.readLine();
		connection.printLine(msg + " World!");
		connection.writeInt(100);
		connection.write("Das ist ein laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaanger Test".getBytes());
		connection.close();
	}

}
