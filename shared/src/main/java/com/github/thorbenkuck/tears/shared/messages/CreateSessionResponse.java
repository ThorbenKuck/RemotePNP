package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;

import java.io.Serializable;

public final class CreateSessionResponse implements Serializable {

	private final GameSession gameSession;
	private final boolean success;
	private final String message;
	private static final long serialVersionUID = -4816524448198205175L;

	public CreateSessionResponse(GameSession gameSession) {
		this.gameSession = gameSession;
		this.success = true;
		this.message = "No message";
	}

	public CreateSessionResponse(String message) {
		this.gameSession = null;
		this.success = false;
		this.message = message;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	public boolean isSuccessful() {
		return success;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "CreateSessionResponse{" +
				"gameSession=" + gameSession +
				", success=" + success +
				", message='" + message + '\'' +
				'}';
	}
}
