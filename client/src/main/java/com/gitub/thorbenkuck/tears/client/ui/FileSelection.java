package com.gitub.thorbenkuck.tears.client.ui;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class FileSelection {

	public static Optional<File> selectSoundFileToOpen(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Audio files", "*.mp3", "*.aac", "*.wav");
		fileChooser.getExtensionFilters().add(extFilter);
		return Optional.ofNullable(fileChooser.showOpenDialog(stage));
	}

}
