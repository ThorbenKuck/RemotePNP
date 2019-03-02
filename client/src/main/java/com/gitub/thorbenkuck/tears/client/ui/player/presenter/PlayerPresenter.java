package com.gitub.thorbenkuck.tears.client.ui.player.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.UserPresenter;
import com.gitub.thorbenkuck.tears.client.ui.player.view.PlayerView;

public interface PlayerPresenter extends UserPresenter {

	static PlayerPresenter create(Repository repository, CharacterRepository characterRepository, MetaController metaController) {
		return new PlayerPresenterImpl(repository, characterRepository, metaController);
	}

	PlayerView getView();

	void updateUser();

	void privateRoll(String text);

	void dispatchChatMessage(String message);

	void publicRoll(String text);

	void updateBaseValues();

	void updateAttributes();

	void removeAttribute(Attribute item);

	void removeBaseValue(BaseValue item);

	void backToServerView();

	void killAllSounds();

}
