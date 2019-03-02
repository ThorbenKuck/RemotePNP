package com.gitub.thorbenkuck.tears.server;

import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.gitub.thorbenkuck.tears.server.network.ServerUser;

import java.util.*;

public class UserManagement {

	private final Map<String, ServerUser> userMap = new HashMap<>();

	private boolean isFree(String userName) {
		synchronized (userMap) {
			return userMap.get(userName) == null;
		}
	}

	public void logout(ServerUser serverUser) {
		if(serverUser == null || serverUser.toShared() == null) {
			return;
		}
		if(!isFree(serverUser.toShared().getUserName())) {
			synchronized (userMap) {
				userMap.remove(serverUser.toShared().getUserName());
			}
		}
	}

	public boolean login(ServerUser serverUser) {
		if(!isFree(serverUser.toShared().getUserName())) {
			return false;
		}

		synchronized (userMap) {
			if(!isFree(serverUser.toShared().getUserName())) {
				return false;
			}

			userMap.put(serverUser.toShared().getUserName(), serverUser);
		}
		return true;
	}

	public void distribute(Object arg) {
		List<ServerUser> users;
		synchronized (userMap) {
			users = new ArrayList<>(userMap.values());
		}

		users.forEach(serverUser -> serverUser.notify(arg));
	}

	public Optional<ServerUser> find(User target) {
		synchronized (userMap) {
			return Optional.ofNullable(userMap.get(target.getUserName()));
		}
	}
}
