package com.gitub.thorbenkuck.tears.client.ui.setup.presenter;

import com.github.thorbenkuck.tears.shared.Settings;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import com.google.common.eventbus.EventBus;

import java.io.File;
import java.util.List;

public interface SetupPresenter extends Presenter {

	static SetupPresenter create(MetaController metaController, CharacterRepository characterRepository, Repository repository, EventBus eventBus, Settings settings) {
		return new SetupPresenterImpl(metaController, characterRepository, repository, eventBus, settings);
	}

	SetupView getView();

	void createNewCharacter();

	void connect(String server, String username, Character selectedCharacter);

	void loadCharacters(List<File> files);
}
