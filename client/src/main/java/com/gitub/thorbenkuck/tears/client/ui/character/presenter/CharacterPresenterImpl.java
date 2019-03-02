package com.gitub.thorbenkuck.tears.client.ui.character.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.character.view.CharacterView;

import java.util.List;

class CharacterPresenterImpl implements CharacterPresenter {

	private CharacterView view;
	private final CharacterRepository characterRepository;
	private final MetaController metaController;

	CharacterPresenterImpl(CharacterRepository characterRepository, MetaController metaController) {
		this.characterRepository = characterRepository;
		this.metaController = metaController;
	}

	@Override
	public <T extends View> void inject(T view) {
		this.view = (CharacterView) view;
	}

	@Override
	public CharacterView getView() {
		return view;
	}

	@Override
	public void createCharacter(String characterName, int characterLife, int characterMental, List<BaseValue> baseValues, List<Attribute> attributeList) {
		Character character = new Character(characterName, attributeList, baseValues, characterLife, characterMental);
		String path = view.selectStoreFile((characterName.toLowerCase().charAt(0)) + characterName.substring(1) + ".character");

		if(path.isEmpty()) {
			return;
		}

		if(!path.endsWith(".character")) {
			path = path + ".character";
		}

		if(!characterRepository.storeAndAdd(character, path)) {
			view.setFeedback("Die Datei konnte nicht gespeichert werden :/");
		} else {
			destroy();
		}
	}

	@Override
	public void cleanUp() {
		view = null;
	}

	@Override
	public void setup() {

	}
}
