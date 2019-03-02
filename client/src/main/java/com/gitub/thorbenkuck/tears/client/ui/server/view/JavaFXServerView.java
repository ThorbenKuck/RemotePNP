package com.gitub.thorbenkuck.tears.client.ui.server.view;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.ui.FXUtils;
import com.gitub.thorbenkuck.tears.client.ui.StyleSheetCache;
import com.gitub.thorbenkuck.tears.client.ui.server.presenter.ServerPresenter;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

class JavaFXServerView implements ServerView {

	private ServerPresenter presenter;
	private Parent root;
	private Scene scene;
	private Stage stage;
	@FXML private TextField gameSessionName;
	@FXML private ListView<GameSession> sessionList;
	@FXML private Label feedback;

	JavaFXServerView(ServerPresenter presenter) {
		this.presenter = presenter;
	}

	@FXML
	public void createSession(Event event) {
		event.consume();
		String sessionName = gameSessionName.getText();

		presenter.createNewSession(sessionName);
	}

	@FXML
	public void joinGameSession(Event event) {
		event.consume();
		ObservableList<GameSession> list = sessionList.getSelectionModel()
				.getSelectedItems();

		if(list.isEmpty() || list.size() > 1){
			setErrorFeedback("Du musst genau einen Server auswählen");
		} else {
			presenter.join(list.get(0));
		}
	}

	@FXML
	private void keyPressedOnCreate(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			event.consume();
			createSession(event);
		}
	}

	@FXML
	private void joinMouseClicked(MouseEvent event) {
		if(event.isPrimaryButtonDown() && event.getClickCount() == 2) {
			event.consume();
			joinGameSession(event);
		}
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
	public ServerPresenter getPresenter() {
		return presenter;
	}

	@Override
	public void setSessionList(List<GameSession> gameSessions) {
		FXUtils.runOnApplicationThread(() -> {
			sessionList.getItems().clear();
			sessionList.getItems()
					.addAll(gameSessions);
		});
	}

	@Override
	public void close() {
		stage = null;
		presenter = null;
		root = null;
		scene = null;
	}

	@Override
	public void setup() {
		Logger.debug("Loading main menu fxml file");
		URL location = SetupView.class.getClassLoader().getResource("main_menu.fxml");
		Logger.trace("Constructing FXMLLoader");
		FXMLLoader fxmlLoader = new FXMLLoader(location);
		Logger.trace("Setting Controller");
		fxmlLoader.setController(this);

		Parent parent;
		try {
			Logger.debug("Loading fxml");
			parent = fxmlLoader.load();
		} catch (Exception e) {
			Logger.catching(e);
			throw new IllegalStateException(e);
		}

		Logger.trace("fxml is loaded. storing information");
		FXUtils.runOnApplicationThread(() -> {
			Logger.trace("Storing root information");
			root = parent;
			Logger.trace("Constructing Scene");
			scene = new Scene(root);
		});
	}

	@Override
	public void setTitle(String title) {
		stage.setTitle(title);
	}

	@Override
	public void display() {
		Logger.trace("Displaying window");
		FXUtils.runOnApplicationThread(() -> {
			Logger.trace("Setting scene");
			stage.setScene(scene);
			root.getStylesheets().clear();
			root.getStylesheets().add(StyleSheetCache.pathToMainMenu());
			stage.setResizable(false);
			stage.setMaximized(false);
			if(!stage.isShowing()) {
				Logger.trace("Attempting to show..");
				stage.show();
			}
			Logger.info("Showing JavaFXServerView");
		});
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
