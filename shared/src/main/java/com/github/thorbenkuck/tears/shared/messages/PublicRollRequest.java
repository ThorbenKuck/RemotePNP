package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class PublicRollRequest implements Serializable {

	private final User origin;
	private final int numberOfSides;
	private final int amountRolls;
	private final GameSession gameSession;
	private static final long serialVersionUID = -8488547855671015793L;

	public PublicRollRequest(User origin, int numberOfSides, int amountRolls, GameSession gameSession) {
		this.origin = origin;
		this.numberOfSides = numberOfSides;
		this.amountRolls = amountRolls;
		this.gameSession = gameSession;
	}

	public User getOrigin() {
		return origin;
	}

	public int getNumberOfSides() {
		return numberOfSides;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public String toString() {
		return "PublicRollRequest{" +
				"origin=" + origin +
				", numberOfSides=" + numberOfSides +
				", gameSession=" + gameSession +
				'}';
	}

	public int getAmountRolls() {
		return amountRolls;
	}
}
