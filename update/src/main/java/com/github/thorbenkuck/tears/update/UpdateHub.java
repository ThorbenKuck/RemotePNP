package com.github.thorbenkuck.tears.update;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.CurrentVersionRequest;
import com.github.thorbenkuck.tears.shared.messages.CurrentVersionResponse;
import com.github.thorbenkuck.tears.shared.messages.UpdateLauncherRequest;
import com.github.thorbenkuck.tears.shared.messages.UpdateLauncherResponse;
import com.github.thorbenkuck.tears.shared.network.ServerContainer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateHub {

	private final File versionFile;
	private final ServerContainer serverContainer;
	private final Map<Integer, String> descriptions = new HashMap<>();
	private int currentVersion = -1;
	private LocalDateTime lastCheck;
	private byte[] currentJarBytes;

	public UpdateHub(int port) throws IOException {
		versionFile = checkVersionFile();
		currentJarBytes = getData();
		serverContainer = new ServerContainer(port);
		serverContainer.getCommunicationRegistration().register(UpdateLauncherRequest.class, (connection, o) -> {
			connection.session().send(new UpdateLauncherResponse(getData()));
		});
		serverContainer.getCommunicationRegistration().register(CurrentVersionRequest.class, (connection, o) -> {
			int currentVersion = getCurrentVersion();
			Logger.debug("[UPDATE] Current Version=" + currentVersion);
			connection.session().send(new CurrentVersionResponse(currentVersion, descriptions.getOrDefault(currentVersion, "No Description available")));
		});
		serverContainer.getCommunicationRegistration().setDefaultHandler((connection, o) -> {
			try {
				connection.write("Access Denied".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private File checkVersionFile() {
		URL url = UpdateHub.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			File folder = new File(url.toURI()).getParentFile();
			File target = new File(folder.getPath() + "/version.history");
			if (!target.exists()) {
				Logger.debug("Version file not set. Creating ..");
				try {
					if (!target.createNewFile()) {
						throw new IllegalStateException("Could not create the file " + target);
					}
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
			Logger.debug("Version target: " + target.getPath());
			return target;
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private int getCurrentVersion() {
		checkVersionFile();
		Logger.debug("Attempting to read data");
		synchronized (this) {
			if (lastCheck != null) {
				Logger.trace("Already checked. Checking timestamp..");
				Duration dur = Duration.between(lastCheck, LocalDateTime.now());
				long minutes = dur.toMinutes();
				if (minutes < 2) {
					Logger.trace("Timestamp is less than 2 Minutes. No need for recheck");
					return currentVersion;
				}
			}
		}
		try {
			List<String> list = Files.readAllLines(versionFile.toPath());
			int i = -1;

			for (String s : list) {
				if (!s.isEmpty()) {
					if (s.contains(":")) {
						Logger.trace("Entry contains description");
						try {
							String[] split = s.split(":", 2);
							int temp = Integer.parseInt(split[0]);
							descriptions.put(temp, split[1]);
							Logger.debug("version=" + temp + ", description=" + split[1]);
							if (temp > i) {
								Logger.trace("Found new higher version: " + temp);
								i = temp;
							}
						} catch (NumberFormatException e) {
							// Ignore this exception. If it happens
							// we have a faulty entry in our version
							// file. We cannot correct this, so log
							// it.
							Logger.catching(e);
						}
					} else {

						try {
							int temp = Integer.parseInt(s);
							if (temp > i) {
								Logger.trace("Found new higher version: " + temp);
								i = temp;
							}
						} catch (NumberFormatException e) {
							// Ignore this exception. If it happens
							// we have a faulty entry in our version
							// file. We cannot correct this, so log
							// it.
							Logger.catching(e);
						}
					}
				}
			}

			lastCheck = LocalDateTime.now();

			if(i != currentVersion) {
				readVersion(i);
				currentVersion = i;
			}

			return i;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void readVersion(int version) {
		Logger.debug("New Version found. Starting reading.");
		String jarPath = "RemoteTEARS-Client_" + version + ".jar";
		Logger.debug("Determined " + jarPath + " to be read");

		File jarFile = new File(versionFile.getParent() + "/" + jarPath);

		if (!jarFile.exists()) {
			Logger.error("Could not locate: " + jarFile.getAbsolutePath());
			return;
		}

		try {
			Logger.debug("Reading " + jarPath + " .. ");
			currentJarBytes = Files.readAllBytes(jarFile.toPath());
			Logger.debug("Done reading");
		} catch (IOException e) {
			Logger.catching(e);
			Logger.warn("Using last known bytes!");
		}
	}

	private byte[] getData() {
		Logger.debug("Checking stored data.");
		int version = getCurrentVersion();
		Logger.trace("Current version=" + version);

		if (version == -1) {
			Logger.info("No Version found.");
			return new byte[0];
		}

		if (version == currentVersion) {
			Logger.trace("No change detected. Returning current data.");
			return currentJarBytes;
		} else {
			readVersion(currentVersion);
		}

		return currentJarBytes;

	}

	public void listen() {
		serverContainer.accept();
	}
}
