package com.gitub.thorbenkuck.tears.client.ui.setup.view;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.gitub.thorbenkuck.tears.client.ui.FXUtils;
import com.gitub.thorbenkuck.tears.client.ui.StyleSheetCache;
import com.gitub.thorbenkuck.tears.client.ui.setup.presenter.SetupPresenter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

class JavaFXSetupView implements SetupView {

	private SetupPresenter setupPresenter;
	private Parent root;
	private Scene scene;
	private Stage currentStage;
	@FXML
	private TextField server;
	@FXML
	private TextField userName;
	@FXML
	private ChoiceBox<Character> selectedCharacter;
	@FXML
	private Label feedback;
	@FXML
	private Button loginButton;

	JavaFXSetupView(SetupPresenter setupPresenter) {
		this.setupPresenter = setupPresenter;
	}

	private Character getSelectedCharacter() {
		return selectedCharacter.getSelectionModel().getSelectedItem();
	}

	private void dispatchLogin() {
		if (server.getText().isEmpty()) {
			setErrorFeedback("Kein Server angegeben!");
			return;
		}
		if (userName.getText().isEmpty()) {
			setErrorFeedback("Kein Name angegeben!");
			return;
		}

		if (getSelectedCharacter() == null) {
			setErrorFeedback("Kein Character ausgewählt!");
			return;
		}

		setupPresenter.connect(server.getText(), userName.getText(), getSelectedCharacter());
	}

	@FXML
	private void keyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			event.consume();
			dispatchLogin(event);
		}
	}

	@Override
	public SetupPresenter getPresenter() {
		return setupPresenter;
	}

	@FXML
	public void dispatchLogin(Event actionEvent) {
		actionEvent.consume();
		dispatchLogin();
	}

	@FXML
	public void loadCharacter(ActionEvent actionEvent) {
		actionEvent.consume();

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Character files (*.character)", "*.character");
		fileChooser.getExtensionFilters().add(extFilter);
		List<File> files = fileChooser.showOpenMultipleDialog(currentStage);

		if (files != null && !files.isEmpty()) {
			setupPresenter.loadCharacters(files);
		} else {
			setFeedback("Keine Daten geladen!");
		}
	}

	@FXML
	public void newCharacter(ActionEvent actionEvent) {
		actionEvent.consume();
		setupPresenter.createNewCharacter();
	}

	@Override
	public void disableCharacterSelection() {
		selectedCharacter.getItems().clear();
		selectedCharacter.setDisable(true);
	}

	@Override
	public void setFeedback(String message) {
		FXUtils.runOnApplicationThread(() -> {
			feedback.setTextFill(Color.BLACK);
			feedback.setText(message);
		});
	}

	@Override
	public void setErrorFeedback(String message) {
		FXUtils.runOnApplicationThread(() -> {
			feedback.setTextFill(Color.RED);
			feedback.setText(message);
		});
	}

	@Override
	public void setCharacters(List<Character> all) {
		selectedCharacter.getItems().clear();
		selectedCharacter.getItems().setAll(all);
		selectedCharacter.setDisable(false);
	}

	@Override
	public void clearServer() {
		server.clear();
	}

	@Override
	public void setServer(String s) {
		FXUtils.runOnApplicationThread(() -> {
			if (!s.equals("")) {
				server.setText(s);
				userName.requestFocus();
			}
		});
	}

	@Override
	public void setUsername(String s) {
		FXUtils.runOnApplicationThread(() -> {
			if (!s.equals("")) {
				userName.setText(s);
				if (!server.getText().isEmpty()) {
					selectedCharacter.requestFocus();
				}
			}
		});
	}

	@Override
	public void trySelectCharacter(String s) {
		Platform.runLater(() -> selectedCharacter.getItems().forEach(character -> {
			if (character.getCharacterName().equals(s)) {
				selectedCharacter.getSelectionModel().select(character);
				if (!server.getText().isEmpty() && !userName.getText().isEmpty()) {
					loginButton.requestFocus();
				}
			}
		}));
	}

	@Override
	public void close() {
		currentStage = null;
		setupPresenter = null;
		root = null;
		scene = null;
	}

	@Override
	public void setup() {
		URL location = SetupView.class.getClassLoader().getResource("setup.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(location);
		fxmlLoader.setController(this);
		Parent parent;
		try {
			parent = fxmlLoader.load();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		FXUtils.runOnApplicationThread(() -> {
			root = parent;
			scene = new Scene(root);
		});
	}

	@Override
	public void display() {
		FXUtils.runOnApplicationThread(() -> {
			currentStage.setScene(scene);
			currentStage.setResizable(false);
			currentStage.setMaximized(false);
			root.getStylesheets().clear();
			root.getStylesheets().add(StyleSheetCache.pathToSetup());
			currentStage.show();
		});
	}

	@Override
	public void setStage(Stage stage) {
		this.currentStage = stage;
	}
}
