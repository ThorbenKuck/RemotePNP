package com.gitub.thorbenkuck.tears.client.ui.notes.view;

import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.notes.presenter.NotesPresenter;

public interface NotesView extends View {

	static NotesView create(Presenter presenter) {
		return new JavaFXNotesView((NotesPresenter) presenter);
	}

	void setNotes(String text);

	String getName();

	void setName(String name);

	void setStored(boolean stored);

	NotesPresenter getPresenter();

	boolean isPublicAvailable();

	boolean isGMAvailable();

	void setPublicAvailable(boolean b);

	void setGMAvailable(boolean b);
}
