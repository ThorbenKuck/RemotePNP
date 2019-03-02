package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class DetailPlayerTab extends Tab {

	public DetailPlayerTab(User user, boolean player) {
		super();

		setText(user.getUserName() + " Informationen");

		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		constructBaseInformation(user, gridPane);

		if(player) {
			constructPlayerViewNotes(user, gridPane);
		} else {
			constructGMViewNotes(user, gridPane);
		}

		Parent values = constructValues(user, player);
		Parent content;

		if(values != null) {
			SplitPane temp = new SplitPane();
			temp.setOrientation(Orientation.VERTICAL);
			temp.getItems().addAll(vGrow(hGrow(gridPane)), hGrow(values));
			content = temp;
		} else {
			content = gridPane;
		}

//		ScrollPane content = new ScrollPane(hGrow(new VBox(hGrow(content), hGrow(values))));
//		content.setFitToWidth(true);

		setContent(content);
	}

	private void constructBaseInformation(User user, GridPane content) {
		Label username = new Label(user.getCharacter().getCharacterName());
		Label maxLive = new Label(Integer.toString(user.getCharacter().getLife()));
		Label maxMental = new Label(Integer.toString(user.getCharacter().getMentalHealth()));

		content.add(new Label("Character Name:"), 0, 0);
		content.add(username, 1, 0);
		content.add(new Label("Max. Leben:"), 0, 1);
		content.add(maxLive, 1, 1);
		content.add(new Label("Max. MG:"), 0, 2);
		content.add(maxMental, 1, 2);
	}

	private HBox createSpacer() {
		HBox hBox = new HBox();
		hBox.setPrefWidth(0);
		hBox.setPrefHeight(0);
		HBox.setHgrow(hBox, Priority.ALWAYS);

		return hBox;
	}

	private <T extends Node> T hGrow(T node) {
		HBox.setHgrow(node, Priority.ALWAYS);
		return node;
	}

	private <T extends Node> T vGrow(T node) {
		VBox.setVgrow(node, Priority.ALWAYS);
		return node;
	}

	private Parent constructValues(User user, boolean listViewsDisabled) {
		if(user.getCharacter().getBaseValues().isEmpty() && user.getCharacter().getAttributeList().isEmpty()) {
			return null;
		}
		ListView<BaseValue> baseValueListView = new ListView<>(FXCollections.observableArrayList(user.getCharacter().getBaseValues()));
		baseValueListView.setEditable(!listViewsDisabled);
		VBox.setVgrow(baseValueListView, Priority.ALWAYS);

		ListView<Attribute> attributeListView = new ListView<>(FXCollections.observableArrayList(user.getCharacter().getAttributeList()));
		attributeListView.setEditable(!listViewsDisabled);
		VBox.setVgrow(attributeListView, Priority.ALWAYS);

		VBox baseValueContainer = hGrow(new VBox(hGrow(new HBox(createSpacer(), new Label("Attribute"), createSpacer())), hGrow(baseValueListView)));
		VBox attributeContainer = hGrow(new VBox(hGrow(new HBox(createSpacer(), new Label("Fertigkeiten"), createSpacer())), hGrow(attributeListView)));

		return hGrow(new HBox(baseValueContainer, attributeContainer));
	}

	private void constructPlayerViewNotes(User user, GridPane grid) {
		ListView<NotesInformation> notes = NotesUtils.constructPublicNotesList(user.getCharacter(), notesInformation -> new NotesDialog(notesInformation).show());
		notes.setMaxHeight(1080);

		Label label = new Label("Notizen");
		label.setMinWidth(70);

		grid.add(label, 0, 5);
		grid.add(hGrow(notes), 1, 5);
	}

	private void constructGMViewNotes(User user, GridPane grid) {
		ListView<NotesInformation> notes = NotesUtils.constructGMNotesList(user.getCharacter(), notesInformation -> new NotesDialog(notesInformation).show());
		notes.setMaxHeight(1080);

		Label label = new Label("Notizen");
		label.setMinWidth(70);

		grid.add(label, 0, 5);
		grid.add(hGrow(notes), 1, 5);
	}
}
