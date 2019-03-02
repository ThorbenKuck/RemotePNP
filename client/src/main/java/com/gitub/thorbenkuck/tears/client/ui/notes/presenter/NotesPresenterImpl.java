package com.gitub.thorbenkuck.tears.client.ui.notes.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.ui.UserPresenter;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.notes.view.NotesView;

public class NotesPresenterImpl implements NotesPresenter {

	private NotesView notesView;
	private final Repository repository;
	private NotesInformation notesInformation;
	private UserPresenter userPresenter;

	public NotesPresenterImpl(Repository repository) {
		this.repository = repository;
	}

	@Override
	public <T extends View> void inject(T view) {
		notesView = (NotesView) view;
	}

	@Override
	public void storeNotes(String notes) {
		Logger.debug("Storing notes..");
		if(userPresenter == null) {
			Logger.error("No UserPresenter set!");
		} else {
			notesInformation.getNotes().setContent(notes);
			notesInformation.setPublicVisible(getView().isPublicAvailable());
			notesInformation.setGMVisible(getView().isGMAvailable());
			notesInformation.setName(getView().getName());

			repository.get(User.class).getCharacter().getRepository().update(notesInformation);
			userPresenter.storeUser();
			getView().setStored(true);
		}
	}

	@Override
	public NotesView getView() {
		return notesView;
	}

	@Override
	public void cleanUp() {
		notesView = null;
	}

	@Override
	public void setup() {
		getView().setNotes("Lade die Notizen...");
	}

	@Override
	public void afterDisplay() {
		getView().setStored(true);
	}

	@Override
	public void setUserPresenter(UserPresenter userPresenter) {
		this.userPresenter = userPresenter;
	}

	@Override
	public void setNotesInformation(NotesInformation notesInformation) {
		this.notesInformation = notesInformation;
		getView().setNotes(notesInformation.getNotes().getContent());
		getView().setPublicAvailable(notesInformation.publicVisible());
		getView().setGMAvailable(notesInformation.visibleForGM());
		getView().setName(notesInformation.getName());
	}
}
