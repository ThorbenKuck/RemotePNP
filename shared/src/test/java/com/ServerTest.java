package com;

import com.github.thorbenkuck.tears.shared.network.ServerContainer;

import java.io.IOException;

public class ServerTest {

	public static void main(String[] args) throws IOException {
		ServerContainer serverContainer = new ServerContainer(8801);
		serverContainer.getCommunicationRegistration().register(String.class, (a, b) -> {
			try {
				System.out.println("Empfangen");
				a.write(serverContainer.getObjectEncoder().apply("Empfangen: " + b));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		serverContainer.accept();
	}

}
