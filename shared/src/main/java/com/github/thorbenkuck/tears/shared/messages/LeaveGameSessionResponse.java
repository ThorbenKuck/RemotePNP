package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class LeaveGameSessionResponse implements Serializable {

	private static final long serialVersionUID = 2778318175787725928L;
	private final GameSession gameSession;

	public LeaveGameSessionResponse(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public String toString() {
		return "LeaveGameSessionResponse{" +
				"gameSession=" + gameSession +
				'}';
	}
}
