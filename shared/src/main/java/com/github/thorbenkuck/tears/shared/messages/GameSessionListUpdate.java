package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;
import java.util.List;

public final class GameSessionListUpdate implements Serializable {

	private final List<GameSession> gameSessions;
	private static final long serialVersionUID = -3888742463842057068L;

	public GameSessionListUpdate(List<GameSession> gameSessions) {
		this.gameSessions = gameSessions;
	}

	public List<GameSession> getGameSessions() {
		return gameSessions;
	}

	@Override
	public String toString() {
		return "GameSessionListUpdate{" +
				"gameSessions=" + gameSessions +
				'}';
	}
}
