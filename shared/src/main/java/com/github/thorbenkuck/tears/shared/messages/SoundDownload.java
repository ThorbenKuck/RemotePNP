package com.github.thorbenkuck.tears.shared.messages;

import java.io.Serializable;

public final class SoundDownload implements Serializable {

	private static final long serialVersionUID = -3901244128425931822L;
	private final String id;
	private final String fileName;
	private final byte[] data;

	public SoundDownload(String id, String fileName, byte[] data) {
		this.id = id;
		this.fileName = fileName;
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getData() {
		return data;
	}

	public String getId() {
		return id;
	}
}
