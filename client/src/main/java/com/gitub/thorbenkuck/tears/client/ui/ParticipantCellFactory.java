package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ParticipantCellFactory implements Callback<ListView<User>, ListCell<User>> {

	private final Consumer<String> dispatchChatMessage;
	private final Consumer<String> appendChatMessage;
	private final BiConsumer<Character, String> userBiConsumer;
	private final Stage stage;

	public ParticipantCellFactory(Stage stage, Consumer<String> appendChatMessage, BiConsumer<Character, String> userBiConsumer, Consumer<String> dispatchChatMessage) {
		this.stage = stage;
		this.dispatchChatMessage = dispatchChatMessage;
		this.appendChatMessage = appendChatMessage;
		this.userBiConsumer = userBiConsumer;
	}

	@Override
	public ListCell<User> call(ListView<User> lv) {
		ListCell<User> cell = new CustomListCell();

		ContextMenu contextMenu = new ContextMenu();

		MenuItem showNotes = new MenuItem("Zeige Notizen");
		showNotes.setDisable(true);
		showNotes.setOnAction(event -> {
			event.consume();
			User item = cell.getItem();
			dispatchChatMessage.accept("/showNotes " + item.getUserName());
		});

		MenuItem privateMessage = new MenuItem("Private Nachricht");
		privateMessage.setOnAction(event -> {
			event.consume();
			User item = cell.getItem();
			if(item.getUserName().contains(" ")) {
				dispatchChatMessage.accept("Nur Nutzern ohne Leerzeichen im Namen können private Nachrichten geschickt werden! Looking at you " + item.getUserName() + " ..");
				return;
			}
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Private Nachricht");
			dialog.setContentText("Wie lautet deine Nachricht?");
			Optional<String> optional = dialog.showAndWait();
			optional.ifPresent(msg -> dispatchChatMessage.accept("/private " + item.getUserName() + " " + msg));
		});

		MenuItem download = new MenuItem("Download");

		download.setOnAction(event -> {
			Character character = cell.getItem().getCharacter();
			String fileName = (character.getCharacterName().toLowerCase().charAt(0)) + character.getCharacterName().substring(1) + ".character";

			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Character files (*.character)", "*.character");
			fileChooser.getExtensionFilters().add(extFilter);
			fileChooser.setInitialFileName(fileName);
			File file = fileChooser.showSaveDialog(stage);

			if(file != null) {
				userBiConsumer.accept(character, file.getAbsolutePath());
				appendChatMessage.accept("Charakter " + character.getCharacterName() + " gespeichert in " + file.getAbsolutePath());
			}
		});

		contextMenu.getItems().addAll(showNotes, privateMessage, download);

		cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
			if (isNowEmpty) {
				cell.setContextMenu(null);
			} else {
				cell.setContextMenu(contextMenu);
			}
		});

		return cell;
	}

	private class CustomListCell extends ListCell<User> {
		@Override
		protected void updateItem(User item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setGraphic(null);
			} else {
				setGraphic(new Label(item.getUserName() + " (" + item.getCharacter().getCharacterName() + ")"));
			}
		}
	}
}
