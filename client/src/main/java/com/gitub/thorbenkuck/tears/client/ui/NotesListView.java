package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class NotesListView {

	private final Stage stage;
	private final StackPane anchorPane = new StackPane();

	private void render(ObservableList<? extends NotesInformation> notesInformationList) {
		anchorPane.getChildren().clear();
		anchorPane.getChildren().add(Markdown.toWebView(notesInformationList.get(0).getNotes()));
	}

	public NotesListView(User user) {
		stage = new Stage();
		SplitPane root = new SplitPane();

		HBox.setHgrow(root, Priority.ALWAYS);
		HBox.setHgrow(anchorPane, Priority.ALWAYS);

		root.setOrientation(Orientation.HORIZONTAL);

		ListView<NotesInformation> left = new ListView<>(FXCollections.observableArrayList(user.getCharacter().getRepository().all()));
		left.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		left.getSelectionModel().getSelectedItems().addListener((ListChangeListener<NotesInformation>) c -> render(c.getList()));
		left.getSelectionModel().select(0);

		root.getItems().addAll(left, anchorPane);

		stage.setScene(new Scene(root));
	}

	public void show() {
		stage.show();
	}

}
