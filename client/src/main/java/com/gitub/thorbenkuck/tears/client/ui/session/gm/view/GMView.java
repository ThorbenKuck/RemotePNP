package com.gitub.thorbenkuck.tears.client.ui.session.gm.view;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.SessionView;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.session.gm.presenter.GMPresenter;

import java.io.File;
import java.util.List;

public interface GMView extends SessionView {

	void updateSoundBoard();

	GMPresenter getPresenter();

	static View create(Presenter presenter) {
		return new JavaFXGMView((GMPresenter) presenter);
	}

	void appendChatMessage(String message);

	void setPrivateDiceResult(int result);

	void setPublicDiceResult(int result);

	void clearChatLog();

	File requestStorePath();

	void setNotes(List<NotesInformation> notesInformationList);

	void setTitle(String userName);

	void setParticipantsList(List<User> participants);

	void focus();
}
