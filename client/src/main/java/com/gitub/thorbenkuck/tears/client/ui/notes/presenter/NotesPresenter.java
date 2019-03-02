package com.gitub.thorbenkuck.tears.client.ui.notes.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.UserPresenter;

public interface NotesPresenter extends Presenter {

	static NotesPresenter create(Repository repository) {
		return new NotesPresenterImpl(repository);
	}

	void storeNotes(String notes);

	void setUserPresenter(UserPresenter userPresenter);

	void setNotesInformation(NotesInformation notes);
}
