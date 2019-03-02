package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImageToDisplayMessage implements Serializable {

	private final byte[] data;
	private final boolean toAll;
	private final List<String> subset;
	private final GameSession gameSession;
	private static final long serialVersionUID = 3096592519054793332L;

	public ImageToDisplayMessage(byte[] data, GameSession gameSession) {
		this.data = data;
		this.gameSession = gameSession;
		this.toAll = true;
		this.subset = new ArrayList<>();
	}

	public ImageToDisplayMessage(byte[] data, List<String> subset, GameSession gameSession) {
		this.data = data;
		this.gameSession = gameSession;
		this.toAll = false;
		this.subset = subset;
	}

	public byte[] getData() {
		return data;
	}

	public boolean isToAll() {
		return toAll;
	}

	public List<String> getSubset() {
		return subset;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public String toString() {
		return "ImageToDisplayMessage{" +
				"data=" + Arrays.toString(data) +
				", toAll=" + toAll +
				", subset=" + subset +
				", gameSession=" + gameSession +
				'}';
	}
}
