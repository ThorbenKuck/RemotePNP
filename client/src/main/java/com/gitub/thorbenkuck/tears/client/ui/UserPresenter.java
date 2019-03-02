package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;

public interface UserPresenter extends Presenter {

	void store(Character character, String file);

	void storeUser();

	void editNotes(NotesInformation notesInformation);

	void createNewNotes(String s);
}
