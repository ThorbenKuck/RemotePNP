package com.github.thorbenkuck.tears.shared;

import java.io.Serializable;

public final class SoundDownloadAndMapRequest implements Serializable {

	private final byte[] data;
	private final String filename;
	private final String mapName;

	public SoundDownloadAndMapRequest(byte[] data, String filename, String mapName) {
		this.data = data;
		this.filename = filename;
		this.mapName = mapName;
	}

	public byte[] getData() {
		return data;
	}

	public String getFilename() {
		return filename;
	}

	public String getMapName() {
		return mapName;
	}
}
