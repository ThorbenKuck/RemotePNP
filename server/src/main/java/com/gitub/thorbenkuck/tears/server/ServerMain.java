package com.gitub.thorbenkuck.tears.server;

import com.github.thorbenkuck.tears.shared.SystemPropertyParser;
import com.github.thorbenkuck.tears.shared.exceptions.ConnectionEstablishmentFailedException;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.server.network.Server;

public final class ServerMain {

	public static void main(String[] args) {
		try {
			int port = SystemPropertyParser.getPort("server.port", 8880);
			Logger.debug("Starting Server at " + port);
			Server server = Server.at(port);
			Logger.info("Server launched at " + port);
			ServerSetup.setup(server);
			server.run();
		} catch (ConnectionEstablishmentFailedException e) {
			Logger.error("Server not working correctly.");
			throw new IllegalStateException("Could not start the Server!", e);
		}
	}

}
