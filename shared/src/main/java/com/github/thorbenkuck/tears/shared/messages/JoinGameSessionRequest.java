package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class JoinGameSessionRequest implements Serializable {

	private final GameSession gameSession;
	private static final long serialVersionUID = -9101248794576463632L;

	public JoinGameSessionRequest(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public String toString() {
		return "JoinGameSessionRequest{" +
				"gameSession=" + gameSession +
				'}';
	}
}
