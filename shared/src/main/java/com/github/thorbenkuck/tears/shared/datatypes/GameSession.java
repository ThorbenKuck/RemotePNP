package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class GameSession implements Serializable {

	private final String name;
	private final List<User> participants;
	private final User gameMaster;
	private static final long serialVersionUID = 4425274632744870053L;

	public GameSession(String name, List<User> participants, User gameMaster) {
		this.name = name;
		this.participants = participants;
		this.gameMaster = gameMaster;
	}

	public String getName() {
		return name;
	}

	public List<User> getParticipants() {
		return participants;
	}

	public List<User> getAll() {
		List<User> users = getParticipants();
		users.add(gameMaster);
		return users;
	}

	public User getGameMaster() {
		return gameMaster;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GameSession that = (GameSession) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(participants, that.participants) &&
				Objects.equals(gameMaster, that.gameMaster);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, participants, gameMaster);
	}

	@Override
	public String toString() {
		return name + " von " + gameMaster + ": " + participants.size() + " Teilnehmer";
	}

	public String participantsToDetailed() {
		StringBuilder builder = new StringBuilder();

		if(participants.size() == 0) {
			return "[]";
		}

		User first = participants.get(0);

		builder.append("[").append(first.detailedString());

		for(int i = 1 ; i < participants.size() ; i++) {
			builder.append(",").append(participants.get(i).detailedString());
		}

		return builder.append("]").toString();
	}

	public String detailedString() {
		return "GameSession{" + "name=" + name + ", participants=" + participantsToDetailed() + ",gameMaster=" + gameMaster.detailedString() + "}";
	}
}
