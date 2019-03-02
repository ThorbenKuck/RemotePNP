package com.github.thorbenkuck.tears.shared;

import com.github.thorbenkuck.tears.shared.logging.Logger;

public final class SystemPropertyParser {

	private SystemPropertyParser() {
		throw new UnsupportedOperationException("Instantiation");
	}

	public static int getPort(String key, int defaultPort) {
		String rawPort = System.getProperty(key);
		if (rawPort == null) {
			Logger.debug("No port set in system properties.");
			Logger.info("To use a custom port, start the server with the option: -D" + key + "=<your-port-number-here>");
			rawPort = Integer.toString(defaultPort);
		}

		int port;
		try {
			port = Integer.parseInt(rawPort);
			Logger.debug("Using port " + port);
			return port;
		} catch (NumberFormatException e) {
			Logger.error("The provided port is not an integer!");
			Logger.error("Make sure, you provide a real number as a port");
			throw new IllegalArgumentException("The provided port is not an integer! Given: " + rawPort);
		}
	}

	public static String getAddress(String key, String defaultValue) {
		String rawPort = System.getProperty(key);
		if (rawPort == null) {
			return defaultValue;
		} else {
			return rawPort;
		}
	}

}
