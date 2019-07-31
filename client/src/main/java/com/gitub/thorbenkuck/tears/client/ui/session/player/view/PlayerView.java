package com.gitub.thorbenkuck.tears.client.ui.session.player.view;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.SessionView;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.session.player.presenter.PlayerPresenter;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public interface PlayerView extends SessionView {

	static PlayerView create(Presenter presenter) {
		return new JavaFXPlayerView((PlayerPresenter) presenter);
	}

	void tryAcceptDownload(String contextText, String name, Consumer<Boolean> callback);

	void setBaseValues(List<BaseValue> baseValues);

	void setAttributes(List<Attribute> baseValues);

	void setParticipantsList(List<User> participantsList);

	PlayerPresenter getPresenter();

	void appendChatMessage(String message);

	void setPublicDiceResult(int result);

	void setNotes(List<NotesInformation> informationList);

	File requestStorePath();

	void clearChatLog();

	void displayImage(byte[] data);

	void focus();
}
