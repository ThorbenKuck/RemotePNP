package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.util.List;

public interface SessionPresenter extends UserPresenter {
	void setGameSession(GameSession gameSession);

	void privateRoll(String text);

	void sendChatMessage(String msg);

	void setUser(User user);

	User getUser();

	GameSession getGameSession();

	void rollPublicDice(int sides, int amount);

	void createNewNotes(String name);

	void lowerVolume();

	void higherVolume();

	void killAllSounds();

	void mute();

	void setParticipants(List<User> users);
}
