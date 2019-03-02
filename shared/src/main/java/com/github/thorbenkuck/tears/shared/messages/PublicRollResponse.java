package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class PublicRollResponse implements Serializable {

	private final User origin;
	private final int result;
	private final int sides;
	private final GameSession gameSession;
	private static final long serialVersionUID = -7769577537055377841L;

	public PublicRollResponse(User origin, int result, int sides, GameSession gameSession) {
		this.origin = origin;
		this.result = result;
		this.sides = sides;
		this.gameSession = gameSession;
	}

	public User getOrigin() {
		return origin;
	}

	public int getResult() {
		return result;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	public int getSides() {
		return sides;
	}

	@Override
	public String toString() {
		return "PublicRollResponse{" +
				"origin=" + origin +
				", result=" + result +
				", sides=" + sides +
				", gameSession=" + gameSession +
				'}';
	}
}
