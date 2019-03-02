package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class InSessionUpdate implements Serializable {

	private final GameSession gameSession;
	private static final long serialVersionUID = -7464570469219463141L;

	public InSessionUpdate(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	public String toString() {
		return "SessionUpdate{" + gameSession.detailedString() + "}";
	}
}
