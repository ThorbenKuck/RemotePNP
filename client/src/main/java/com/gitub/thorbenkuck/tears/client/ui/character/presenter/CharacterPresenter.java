package com.gitub.thorbenkuck.tears.client.ui.character.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.character.view.CharacterView;

import java.util.List;

public interface CharacterPresenter extends Presenter {

	static CharacterPresenter create(CharacterRepository characterRepository, MetaController metaController) {
		return new CharacterPresenterImpl(characterRepository, metaController);
	}

	CharacterView getView();

	void createCharacter(String characterName, int characterLife, int characterMental, List<BaseValue> baseValues, List<Attribute> attributeList);
}
