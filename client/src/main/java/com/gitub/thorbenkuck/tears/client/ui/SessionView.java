package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public interface SessionView extends View {

	void appendChatMessage(String string);

	void appendSystemChatMessage(String string);

	void appendChatMessage(String string, User sender);

	void yourDiceResult(int sides, int amount, int result);

	void otherDiceResult(int sides, int amount, int result, User sender);

	void setParticipantsList(List<User> participants);

	void clearChatLog();

	void setNotes(List<NotesInformation> all);

	File requestStorePath();

	void setTitle(String userName);

	void setPublicDiceResult(int i);

	void tryAcceptDownload(String contextText, String name, Consumer<Boolean> callback);

	void focus();

	void displayImage(byte[] data);

}
