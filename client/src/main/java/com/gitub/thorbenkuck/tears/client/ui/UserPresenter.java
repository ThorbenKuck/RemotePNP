package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;

public interface UserPresenter extends Presenter {

	void store(Character character, String file);

	void storeUser();

	void editNotes(NotesInformation notesInformation);

	void createNewNotes(String s);

	void sendChatMessage(String message);

	void lowerVolume();

	void higherVolume();

	void mute();

	void killAllSounds();

	void rollPublicDice(int sides, int amount);

	void setUser(User user);

	User getUser();
}
