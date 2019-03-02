package com.gitub.thorbenkuck.tears.client.network;

import com.github.thorbenkuck.tears.shared.messages.GameSessionListUpdate;
import com.gitub.thorbenkuck.tears.client.Repository;

class ClientSetup {

	static void setup(Client client, Repository repository) {
		client.handleSpecific(GameSessionListUpdate.class, message -> repository.add(message.getCore()));
	}

}
