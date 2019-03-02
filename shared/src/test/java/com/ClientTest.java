package com;

import com.github.thorbenkuck.tears.shared.network.ClientContainer;

import java.io.IOException;

public class ClientTest {

	public static void main(String[] args) throws IOException, InterruptedException {
		ClientContainer clientContainer = new ClientContainer("localhost", 8801);
		clientContainer.getCommunicationRegistration().register(String.class, (a, b) -> System.out.println(b));
		clientContainer.send("Hi");

		Thread.sleep(1000);
	}

}
