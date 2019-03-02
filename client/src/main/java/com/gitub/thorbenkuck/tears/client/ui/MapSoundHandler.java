package com.gitub.thorbenkuck.tears.client.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.Optional;

public class MapSoundHandler {

	public Optional<String> apply(Stage stage) {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Sound Mapping");
		dialog.setHeaderText("Verbinde einen eigenes Musik stück mit einem Musik-Titel");


		ButtonType okayButton = new ButtonType("Okay!", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okayButton, ButtonType.CANCEL);
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField nameTextField = new TextField();
		nameTextField.setPromptText("Name");
		Label selectedFile = new Label("No File selected!");
		Button selectFiledButton = new Button("Wähle eine Datei aus");

		selectFiledButton.setOnMouseClicked(mouseEvent -> {
			mouseEvent.consume();
			FileSelection.selectSoundFileToOpen(stage).ifPresent(file -> {
				if (file.exists()) {
					selectedFile.setText(file.getAbsolutePath());
				} else {
					selectedFile.setText("No File selected!");
				}
			});
		});
		grid.add(new Label("Name:"), 0, 0);
		grid.add(nameTextField, 1, 0);
		grid.add(selectedFile, 0, 1);
		grid.add(selectFiledButton, 1, 1);

		Node loginButton = dialog.getDialogPane().lookupButton(okayButton);
		loginButton.setDisable(true);

		nameTextField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(selectedFile.getText().equals("No File selected!") || nameTextField.getText().isEmpty()));
		selectedFile.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(selectedFile.getText().equals("No File selected!") || nameTextField.getText().isEmpty()));

		dialog.getDialogPane().setContent(grid);
		FXUtils.runOnApplicationThread(nameTextField::requestFocus);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okayButton) {
				return new Pair<>(nameTextField.getText(), selectedFile.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		if(result.isPresent()) {
			Pair<String, String> pair = result.get();
			return Optional.of("/mapToFile " + pair.getKey() + ":" + pair.getValue());
		}

		return Optional.empty();
	}

}
