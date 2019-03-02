package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class LeaveGameSessionRequest implements Serializable {

	private static final long serialVersionUID = 8607785564686366337L;
	private final GameSession gameSession;

	public LeaveGameSessionRequest(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public String toString() {
		return "LeaveGameSessionRequest{" +
				"gameSession=" + gameSession +
				'}';
	}
}
