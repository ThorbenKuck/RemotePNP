package com.gitub.thorbenkuck.tears.client.ui.setup.view;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.setup.presenter.SetupPresenter;

import java.util.List;

public interface SetupView extends View {

	static SetupView create(Presenter presenter) {
		return new JavaFXSetupView((SetupPresenter) presenter);
	}

	SetupPresenter getPresenter();

	void disableCharacterSelection();

	void setFeedback(String message);

	void setErrorFeedback(String message);

	void setCharacters(List<Character> all);

	void clearServer();

	void setServer(String s);

	void setUsername(String s);

	void trySelectCharacter(String s);
}
