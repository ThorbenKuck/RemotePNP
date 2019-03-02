package com.gitub.thorbenkuck.tears.client.media;

import com.github.thorbenkuck.tears.shared.SoundDownloadAndMapRequest;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.SoundDownload;
import com.github.thorbenkuck.tears.shared.messages.UploadRequest;
import com.github.thorbenkuck.tears.shared.messages.UploadResponse;
import com.github.thorbenkuck.tears.shared.messages.groups.UploadDone;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.network.Client;
import com.gitub.thorbenkuck.tears.client.ui.SoundBoard;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class DownloadBroker {

	private final Client client;
	private final Consumer<String> out;
	private final Repository repository;

	public DownloadBroker(Client client, Consumer<String> out, Repository repository) {
		this.client = client;
		this.out = out;
		this.repository = repository;
		client.handleSpecific(SoundDownload.class, message -> {
			Logger.debug("Received UploadAccepted");
			actuallyReceiveDownload(message.getCore());
		});
	}

	// ##############################################
	// # Inside call only (real stuff happens here) #
	// ##############################################

	private void actuallyReceiveDownload(SoundDownload core) {
		out.accept("[Fertig] Download " + core.getId());
		SoundBoard.getInstance().integrate(new SoundDownloadAndMapRequest(core.getData(), core.getFileName(), core.getId()));
		client.send(new UploadDone(core.getId()));
	}

	//################################
	//# Those are the entry methods  #
	//# called outside of this class #
	//################################

	public void receiveDownload(UploadRequest request, boolean okay) {
		if(okay) {
			out.accept("[Start] Download " + request.getId());
		}
		client.send(new UploadResponse(request.getId(), okay));
	}

	public void startDownload(File file, String name) {
		byte[] data;

		try {
			data = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			Logger.catching(e);
			out.accept("Wir konnten die Datei: " + file.getAbsolutePath() + " nicht einlesen!");
			return;
		}

		client.send(new UploadRequest(name, file.getName(), data));
		out.accept("Warte auf Antwort der Teilnehmer");
	}

	public void startDownload(File file, User target, String name) {
		byte[] data;

		try {
			data = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			Logger.catching(e);
			out.accept("Wir konnten die Datei: " + file.getAbsolutePath() + " nicht einlesen!");
			return;
		}

		client.send(new UploadRequest(target, name, file.getName(), data));
		out.accept("Warte auf Antwort der Teilnehmer");
	}
}
