package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class SessionDestroyed implements Serializable {

	private final GameSession gameSession;
	private static final long serialVersionUID = 727821165184272070L;

	public SessionDestroyed(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public String toString() {
		return "SessionDestroyed{" +
				"gameSession=" + gameSession +
				'}';
	}
}
