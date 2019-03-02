package com.github.thorbenkuck.tears.update;

import com.github.thorbenkuck.tears.shared.SystemPropertyParser;
import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.IOException;

public class UpdateMain {

	public static void main(String[] args) {
		Logger.debug("Starting UpdateHub.");
		Logger.trace("Parsing port ..");
		int port = SystemPropertyParser.getPort("update.port", 8881);
		Logger.trace("Port " + port + " will be used.");
		UpdateHub updateHub;
		try {
			Logger.debug("Creating UpdateHub at " + port);
			updateHub = new UpdateHub(port);
		} catch (IOException e) {
			Logger.error("Could not start the UpdateHub!");
			Logger.catching(e);
			return;
		}

		Logger.info("UpdateHub listening at port " + port);

		updateHub.listen();
	}

}
