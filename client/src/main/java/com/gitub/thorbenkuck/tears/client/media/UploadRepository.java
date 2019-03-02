package com.gitub.thorbenkuck.tears.client.media;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadRepository {

	private final Map<UUID, Data> mapping = new HashMap<>();
	private static final byte[] EMPTY_BYTES = new byte[0];

	public synchronized byte[] requestFileContent(UUID uuid) throws IOException {
		Data data = mapping.get(uuid);
		if(data != null) {
			return Files.readAllBytes(data.file.toPath());
		}

		return EMPTY_BYTES;
	}

	public UUID add(File file, String name) {
		UUID id;
		synchronized (this) {
			id = UUID.randomUUID();
		}

		Logger.debug("Mapping " + id);
		synchronized (mapping) {
			mapping.put(id, new Data(file, name));
		}

		return id;
	}

	public File getFile(UUID uuid) {
		Logger.debug("Fetching file for " + uuid);
		return mapping.get(uuid).getFile();
	}

	public String getName(UUID uuid) {
		return mapping.get(uuid).getName();
	}

	public class Data {
		private final File file;
		private final String name;

		public Data(File file, String name) {
			this.file = file;
			this.name = name;
		}

		public File getFile() {
			return file;
		}

		public String getName() {
			return name;
		}
	}

}
