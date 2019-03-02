package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.function.Consumer;

public class NotesUtils {

	private static ListView<NotesInformation> construct(List<NotesInformation> informationList, Consumer<NotesInformation> consumer) {
		ListView<NotesInformation> listView = new ListView<>(FXCollections.observableArrayList(informationList));

		if(consumer != null) {
			listView.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
					NotesInformation note = listView.getSelectionModel().getSelectedItem();
					if (note != null) {
						e.consume();
						consumer.accept(note);
					}
				}
			});
		}

		return listView;
	}

	public static ListView<NotesInformation>  constructPublicNotesList(Character character, Consumer<NotesInformation> consumer) {
		return construct(character.getRepository().publicAvailable(), consumer);
	}

	public static ListView<NotesInformation>  constructPrivateNotesList(Character character, Consumer<NotesInformation> consumer) {
		return construct(character.getRepository().all(), consumer);
	}

	public static ListView<NotesInformation>  constructGMNotesList(Character character, Consumer<NotesInformation> consumer) {
		return construct(character.getRepository().dmAvailable(), consumer);
	}

	public static ListView<NotesInformation>  constructPublicNotesList(Character character) {
		return construct(character.getRepository().publicAvailable(), null);
	}

	public static ListView<NotesInformation>  constructPrivateNotesList(Character character) {
		return construct(character.getRepository().all(), null);
	}

	public static ListView<NotesInformation>  constructGMNotesList(Character character) {
		return construct(character.getRepository().dmAvailable(), null);
	}

}
