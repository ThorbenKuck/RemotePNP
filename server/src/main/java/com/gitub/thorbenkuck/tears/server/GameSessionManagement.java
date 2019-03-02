package com.gitub.thorbenkuck.tears.server;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.GameSessionListUpdate;
import com.github.thorbenkuck.tears.shared.messages.LeaveGameSessionResponse;
import com.github.thorbenkuck.tears.shared.messages.PlayerJoinedSession;
import com.github.thorbenkuck.tears.shared.messages.SessionDestroyed;
import com.gitub.thorbenkuck.tears.server.network.ServerUser;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameSessionManagement extends Observable {

	private final Map<String, ServerGameSession> map = new HashMap<>();

	private void tryLeave(String game, ServerUser serverUser) {
		Logger.debug(serverUser + " tries to leave " + game);
		ServerGameSession gameSession;

		synchronized (map) {
			gameSession = map.get(game);
		}

		if (gameSession != null) {
			if (gameSession.getGameMaster().equals(serverUser)) {
				destroyGameSession(game);
			} else {
				gameSession.removeUser(serverUser);
				serverUser.notify(new LeaveGameSessionResponse(gameSession.toShared()));
				gameSession.update();
				setChanged();
				notifyObservers(updateMessage());
			}
		}
	}

	private void destroyGameSession(String key) {
		Logger.debug("GameMaster destroying the game session " + key);
		ServerGameSession gameSession;

		synchronized (map) {
			gameSession = map.remove(key);
			gameSession.allUsers().forEach(user -> user.notify(new SessionDestroyed(gameSession.toShared())));
		}

		setChanged();
		notifyObservers(updateMessage());
	}

	public Optional<ServerGameSession> getGameSessionForGM(ServerUser serverUser) {
		return getForGM(serverUser).findFirst();
	}

	public List<ServerGameSession> findGameSessions(ServerUser serverUser) {
		return copyAll().stream()
				.filter(game -> game.contains(serverUser))
				.collect(Collectors.toList());
	}

	GameSessionListUpdate updateMessage() {
		return new GameSessionListUpdate(copyAll().stream()
				.map(ServerGameSession::toShared)
				.collect(Collectors.toList()));
	}

	public void leaveAll(ServerUser serverUser) {
		Logger.debug("User " + serverUser.toShared() + " leaving all game sessions");
		List<String> toLeave;
		synchronized (map) {
			toLeave = map.entrySet()
					.stream()
					.filter(entry -> entry.getValue().contains(serverUser))
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());

			toLeave.forEach(s -> tryLeave(s, serverUser));
		}
	}

	public void updateFor(ServerUser serverUser) {
		List<ServerGameSession> toUpdate;

		synchronized (map) {
			toUpdate = map.entrySet()
					.stream()
					.filter(entry -> entry.getValue().contains(serverUser))
					.map(Map.Entry::getValue)
					.collect(Collectors.toList());
		}

		toUpdate.forEach(ServerGameSession::update);
	}

	public Optional<ServerGameSession> getGameSession(String name) {
		ServerGameSession gameSession = accessGameSession(name);
		return Optional.ofNullable(gameSession);
	}

	public ServerGameSession accessGameSession(String name) {
		synchronized (map) {
			return map.get(name);
		}
	}

	private Stream<ServerGameSession> getForGM(ServerUser gm) {
		return copyAll().stream()
				.filter(serverGameSession -> serverGameSession.getGameMaster().toShared().getUserName().equals(gm.toShared().getUserName()));
	}

	public void gmPublishAction(ServerUser gm, Serializable message) {
		getForGM(gm).forEachOrdered(session -> session.publish(message));
	}

	public void gmPublishToParticipantsAction(ServerUser gm, Serializable message) {
		getForGM(gm).forEachOrdered(session -> session.toParticipants(message));
	}

	public boolean joinGameSession(String name, ServerUser toJoin) {
		Logger.debug(toJoin + " tries to join " + name);
		Optional<ServerGameSession> serverGameSessionOptional = getGameSession(name);
		if (!serverGameSessionOptional.isPresent()) {
			return false;
		}

		ServerGameSession serverGameSession = serverGameSessionOptional.get();

		if (serverGameSession.contains(toJoin)) {
			return false;
		}

		setChanged();
		serverGameSession.addUser(toJoin);
		notifyObservers(updateMessage());
		serverGameSession.publish(new PlayerJoinedSession(toJoin.toShared()));
		serverGameSession.update();

		return true;
	}

	public boolean exists(String name) {
		return getGameSession(name).isPresent();
	}

	public boolean createNewGameSession(String name, ServerUser gameMaster) {
		if (name == null || exists(name) || name.isEmpty()) {
			return false;
		}

		ServerGameSession serverGameSession = new ServerGameSession(name, gameMaster);

		// synchronize is reentrant. Therefor
		// we can synchronize and sanity check
		// one more time, before adding it.
		// This may not be necessary most of
		// the time, but if another thread
		// adds the exact same game while we
		// create the GameSession, this would
		// cause some problems
		synchronized (map) {
			if (exists(name)) {
				return false;
			}

			map.put(name, serverGameSession);
		}

		setChanged();
		notifyObservers(updateMessage());

		serverGameSession.update();

		return true;
	}

	public List<ServerGameSession> copyAll() {
		synchronized (map) {
			return new ArrayList<>(map.values());
		}
	}

}
