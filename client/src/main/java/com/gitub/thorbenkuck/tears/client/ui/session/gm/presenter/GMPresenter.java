package com.gitub.thorbenkuck.tears.client.ui.session.gm.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.Sound;
import com.gitub.thorbenkuck.tears.client.ui.UserPresenter;
import com.gitub.thorbenkuck.tears.client.ui.session.gm.view.GMView;

import java.io.File;

public interface GMPresenter extends UserPresenter {

	static GMPresenter create(Repository repository, CharacterRepository characterRepository, MetaController metaController) {
		return new GMPresenterImpl(repository, characterRepository, metaController);
	}

	void backToServerView();

	GMView getView();

	void publicRoll(String text, String amount);

	void privateRoll(String text, String amount);

	void sendToAll(byte[] res);

	void sendToSpecific(User user, byte[] res);

	void playOnAll(Sound item);

	void requestSendingOf(File file, String name);

	void requestSendingOf(File file, User target, String name);

	void killAllSounds();
}
