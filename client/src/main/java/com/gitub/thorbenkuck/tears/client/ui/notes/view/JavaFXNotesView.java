package com.gitub.thorbenkuck.tears.client.ui.notes.view;

import com.gitub.thorbenkuck.tears.client.ui.FXUtils;
import com.gitub.thorbenkuck.tears.client.ui.Markdown;
import com.gitub.thorbenkuck.tears.client.ui.notes.presenter.NotesPresenter;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaFXNotesView implements NotesView {

	private final SimpleBooleanProperty stored = new SimpleBooleanProperty(true);
	private NotesPresenter notesPresenter;
	@FXML
	private TextArea rawInput;
	@FXML
	private Tab showTab;
	@FXML
	private Button applyButton;
	@FXML
	private AnchorPane viewRoot;
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab editTab;
	@FXML
	private CheckBox publicCheckBox;
	@FXML
	private CheckBox gmCheckBox;
	@FXML
	private TextField notesNameTextField;
	private Stage stage;
	private SimpleBooleanProperty same = new SimpleBooleanProperty();
	private Scene scene;

	public JavaFXNotesView(NotesPresenter notesPresenter) {
		this.notesPresenter = notesPresenter;
	}

	@FXML
	private void store(ActionEvent actionEvent) {
		if (actionEvent != null) {
			actionEvent.consume();
		}
		notesPresenter.storeNotes(rawInput.getText());
		same.set(true);
	}

	@FXML
	private void applyAndSwitchView(ActionEvent actionEvent) {
		actionEvent.consume();
		render();
		same.set(true);
		tabPane.getSelectionModel().select(showTab);
	}

	private void render() {
		viewRoot.getChildren().clear();
		viewRoot.getChildren().add(new TextArea("Wir rendern die Datei... Eine Sekunde!"));
		WebView webView = Markdown.toWebView(rawInput.getText());
		AnchorPane.setBottomAnchor(webView, 0.0);
		AnchorPane.setTopAnchor(webView, 0.0);
		AnchorPane.setLeftAnchor(webView, 0.0);
		AnchorPane.setRightAnchor(webView, 0.0);
		viewRoot.getChildren().clear();
		viewRoot.getChildren().add(webView);
	}

	@Override
	public String getName() {
		return notesNameTextField.getText();
	}

	@Override
	public void setName(String name) {
		notesNameTextField.setText(name);
	}

	@Override
	public void setStored(boolean stored) {
		this.stored.set(stored);
	}

	@Override
	public NotesPresenter getPresenter() {
		return notesPresenter;
	}

	@Override
	public void close() {
		notesPresenter = null;
		if (stage != null) {
			FXUtils.runOnApplicationThread(() -> {
				stage.close();
				stage = null;
			});
		}
		scene = null;
	}

	@Override
	public void setNotes(String text) {
		rawInput.setText(text);
		same.set(true);
	}

	@Override
	public boolean isPublicAvailable() {
		return publicCheckBox.isSelected();
	}

	@Override
	public boolean isGMAvailable() {
		return gmCheckBox.isSelected();
	}

	@Override
	public void setPublicAvailable(boolean b) {
		publicCheckBox.setSelected(b);
	}

	@Override
	public void setGMAvailable(boolean b) {
		gmCheckBox.setSelected(b);
	}

	@Override
	public void setup() {
		URL location = SetupView.class.getClassLoader().getResource("notes.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(location);
		fxmlLoader.setController(this);

		Parent parent;
		try {
			parent = fxmlLoader.load();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		FXUtils.runOnApplicationThread(() -> {
			render();
			publicCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if(newValue) {
					gmCheckBox.setDisable(true);
					gmCheckBox.setSelected(true);
				} else {
					gmCheckBox.setDisable(false);
				}
			});
			showTab.disableProperty().bind(rawInput.textProperty().isEmpty());
			rawInput.textProperty().addListener((observable, oldValue, newValue) -> {
				same.set(false);
				stored.set(false);
			});
			applyButton.disableProperty().bind(same);
			AtomicBoolean storeEnabled = new AtomicBoolean(false);
			tabPane.getSelectionModel().select(editTab);
			showTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					render();
					storeEnabled.set(false);
				} else {
					storeEnabled.set(true);
				}
			});
			scene = new Scene(parent);
			scene.setOnKeyPressed(keyEvent -> {
				if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.S) {
					store(null);
				}
			});
			stage.setOnCloseRequest(event -> {
				event.consume();
				if (!stored.get()) {
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.initOwner(stage);
					alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
					alert.setTitle("Ungespeicherte Änderungen");
					alert.setHeaderText("Du hast noch ungespeicherte Änderungen!");
					alert.setContentText("Änderungen speichern?");

					alert.showAndWait().ifPresent(result -> {
						if (result == ButtonType.YES) {
							store(null);
							stage.close();
						} else if (result == ButtonType.NO) {
							stage.close();
						}
					});
				} else {
					stage.close();
				}
			});
		});
	}

	@Override
	public void display() {
		FXUtils.runOnApplicationThread(() -> {
			stage.setScene(scene);
			stage.show();
		});
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
