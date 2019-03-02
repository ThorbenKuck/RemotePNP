package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class JoinGameSessionResponse implements Serializable {

	private final boolean successful;
	private static final long serialVersionUID = 1371285586333609322L;
	private final GameSession gameSession;

	public JoinGameSessionResponse(GameSession gameSession) {
		this.gameSession = gameSession;
		this.successful = (gameSession != null);
	}

	public boolean isSuccessful() {
		return successful;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public String toString() {
		return "JoinGameSessionResponse{" +
				"successful=" + successful +
				", gameSession=" + gameSession +
				'}';
	}
}
