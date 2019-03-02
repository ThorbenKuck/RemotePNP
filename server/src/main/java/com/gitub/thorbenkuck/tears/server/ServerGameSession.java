package com.gitub.thorbenkuck.tears.server;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.InSessionUpdate;
import com.github.thorbenkuck.tears.shared.messages.PlayerLeaveEvent;
import com.gitub.thorbenkuck.tears.server.network.ServerUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerGameSession {

	private final GameSession gameSession;
	private final ServerUser gameMaster;
	private final List<ServerUser> participants = new ArrayList<>();
	private final UploadRepository uploadRepository = new UploadRepository();

	public ServerGameSession(String name, ServerUser gameMaster) {
		this.gameSession = new GameSession(name, new ArrayList<>(), gameMaster.toShared());
		this.gameMaster = gameMaster;
	}

	public GameSession toShared() {
		return gameSession;
	}

	public void addUser(ServerUser toJoin) {
		Logger.debug("Adding user " + toJoin + " to " + this);
		gameSession.getParticipants().add(toJoin.toShared());
		synchronized (participants) {
			participants.add(toJoin);
		}
		update();
	}

	public void prepareDownload(byte[] data, String fileName, String id, ServerUser sender) {
		prepareDownload(data, fileName, id, sender, participants);
	}

	public void prepareDownload(byte[] data, String fileName, String id, ServerUser sender, List<ServerUser> toReceive) {
		uploadRepository.add(data, fileName, toReceive, sender, id);
	}

	public ServerUser getGameMaster() {
		return gameMaster;
	}

	public void update() {
		Logger.debug("Collecting information stream to publish..");
		gameSession.getGameMaster().getCharacter().updateBy(gameMaster.toShared().getCharacter());
		Map<String, User> userMap = participants.stream()
				.collect(Collectors.toMap(s -> s.toShared().getUserName(), ServerUser::toShared));

		Logger.debug("Recalculating shared details..");

		for(User gameSessionUser : gameSession.getParticipants()) {
			gameSessionUser.getCharacter().updateBy(userMap.get(gameSessionUser.getUserName()).getCharacter());
		}

		InSessionUpdate inSessionUpdate = new InSessionUpdate(gameSession);
		publish(inSessionUpdate);
	}

	public boolean contains(ServerUser serverUser) {
		synchronized (participants) {
			return participants.contains(serverUser) || gameMaster.equals(serverUser);
		}
	}

	public void removeUser(ServerUser serverUser) {
		synchronized (participants) {
			participants.remove(serverUser);
		}
		Logger.debug("Removing " + serverUser + " from " + this);
		gameSession.getParticipants().remove(serverUser.toShared());
		publish(new PlayerLeaveEvent(serverUser.toShared()));
		update();
	}

	public List<ServerUser> allUsers() {
		List<ServerUser> users;

		synchronized (participants) {
			users = new ArrayList<>(participants);
		}

		users.add(gameMaster);

		return users;
	}

	public void publish(Object o) {
		List<ServerUser> toPublishTo = allUsers();
		Logger.debug("Distributing " + o + " to: " + toPublishTo);
		toPublishTo.forEach(serverUser -> serverUser.notify(o));
	}

	public void toParticipants(Object o) {
		List<ServerUser> toPublishTo;

		synchronized (participants) {
			toPublishTo = new ArrayList<>(participants);
		}

		Logger.debug("Distributing " + o + " to " + toPublishTo);

		toPublishTo.forEach(serverUser -> serverUser.notify(o));
	}

	public ServerUser find(String userName) {
		if(gameMaster.toShared().getUserName().equals(userName)) {
			return gameMaster;
		}

		synchronized (participants) {
			for (ServerUser participant : participants) {
				if(participant.toShared().getUserName().equals(userName)) {
					return participant;
				}
			}
		}
		return null;
	}

	public List<ServerUser> findAll(List<String> userNames) {
		List<ServerUser> result = new ArrayList<>();

		for(String userName : userNames) {
			result.add(find(userName));
		}

		return result;
	}

	public void accept(String id, ServerUser serverUser) {
		uploadRepository.accept(id, serverUser);
	}

	public void reject(String id, ServerUser serverUser) {
		uploadRepository.reject(id, serverUser);
	}

	public void done(String id, ServerUser serverUser) {
		uploadRepository.done(id, serverUser);
	}
}
