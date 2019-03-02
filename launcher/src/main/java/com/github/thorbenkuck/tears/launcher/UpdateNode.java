package com.github.thorbenkuck.tears.launcher;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.CurrentVersionRequest;
import com.github.thorbenkuck.tears.shared.messages.CurrentVersionResponse;
import com.github.thorbenkuck.tears.shared.messages.UpdateLauncherRequest;
import com.github.thorbenkuck.tears.shared.messages.UpdateLauncherResponse;
import com.github.thorbenkuck.tears.shared.network.ClientContainer;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class UpdateNode {

	private final ClientContainer clientContainer;
	private final Semaphore awaiting = new Semaphore(0);
	private int version;
	private String description;
	private byte[] gzipData;

	public UpdateNode(String address, int port) throws IOException {
		this.clientContainer = new ClientContainer(address, port);
		clientContainer.getCommunicationRegistration().register(CurrentVersionResponse.class, (connection, currentVersionResponse) -> {
			version = currentVersionResponse.getVersion();
			Logger.debug("Setting version to " + version);
			description = currentVersionResponse.getUpdateMessage();
			Logger.debug("Setting description to " + description);
			awaiting.release();
		});

		clientContainer.getCommunicationRegistration().register(UpdateLauncherResponse.class, (connection, updateLauncherResponse) -> {
			gzipData = updateLauncherResponse.getData();
			awaiting.release();
		});
	}

	public void listen() {
		clientContainer.listen();
	}

	public int getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public boolean newVersionAvailable(int version) {
		try {
			clientContainer.send(new CurrentVersionRequest());
			awaiting.acquire();
			Logger.debug("Comparing " + this.version + " and local " + version);
			return this.version != -1 && this.version > version;
		} catch (IOException | InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	public void download() {
		try {
			clientContainer.send(new UpdateLauncherRequest());
			awaiting.acquire();
			Logger.info("New Version found!");

			byte[] data = gzipData;
			File jarTarget = new File(new File(UpdateNode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile(), "RemoteTEARS-Client.jar");
			Logger.debug("Trying to delete and create " + jarTarget);
			if(jarTarget.exists()) {
				Logger.debug("Found old version. Deleting ...");
				jarTarget.delete();
			}
			if(!jarTarget.createNewFile()) {
				Logger.error("Could not create the jar file!");
				throw new IllegalStateException("Unable to create the downloaded jar file");
			}

			Logger.debug("Jar file created. Filling ..");
			Files.write(jarTarget.toPath(), data);
			Logger.info("Jar file filled and created");
		} catch (IOException | InterruptedException | URISyntaxException e) {
			Logger.error("Could not decompress the file!");
			Logger.catching(e);
			throw new IllegalStateException(e);
		}

		clientContainer.close();
	}

	public void restart() throws URISyntaxException {
		File jarFile = new File(new File(UpdateNode.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile(), "RemoteTEARS-Client.jar");
		File folder = jarFile.getParentFile();
		File log = new File(folder.getAbsolutePath() + "/protocol.txt");

		final List<String> args = new ArrayList<>();
		args.add(0, "java");
		args.add(1, "-jar");
		args.add(2, jarFile.getAbsolutePath());

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(args.toArray(new String[0]));
			processBuilder.redirectErrorStream(true);
			processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
			processBuilder.start();

		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		Logger.info("Process detached. Preparing to exit application.");
	}
}
