package com.gitub.thorbenkuck.tears.client.ui.character.view;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.character.presenter.CharacterPresenter;

public interface CharacterView extends View {

	static CharacterView create(Presenter presenter) {
		return new JavaFXCharacterView((CharacterPresenter) presenter);
	}

	CharacterPresenter getPresenter();

	void addBaseValue(BaseValue baseValue);

	void addAttribute(Attribute value);

	void setFeedback(String s);

	String selectStoreFile(String name);
}
