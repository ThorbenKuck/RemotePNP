package com.gitub.thorbenkuck.tears.client.ui.session.player.view;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.ui.FXUtils;
import com.gitub.thorbenkuck.tears.client.ui.MapSoundHandler;
import com.gitub.thorbenkuck.tears.client.ui.NotesListView;
import com.gitub.thorbenkuck.tears.client.ui.TextFieldListener;
import com.gitub.thorbenkuck.tears.client.ui.session.AbstractSessionView;
import com.gitub.thorbenkuck.tears.client.ui.session.player.presenter.PlayerPresenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.util.List;

class JavaFXPlayerView extends AbstractSessionView<PlayerPresenter> implements PlayerView {

	private final AttributeAndBaseValueView attributeAndBaseValueView = new AttributeAndBaseValueView(new BaseValueClickHandler());
	private final MapSoundHandler mapSoundHandler = new MapSoundHandler();
	private final ListView<BaseValue> baseValueListView = attributeAndBaseValueView.getBaseValueListView();
	private final ListView<Attribute> attributeListView = attributeAndBaseValueView.getAttributeListView();
	private int imageCount = 0;

	JavaFXPlayerView(PlayerPresenter playerPresenter) {
		super(playerPresenter);
		Logger.info("You are a player");
	}

	private void dispatchAttributeChange() {

	}

	@FXML
	private void attributeClickHandle(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			dispatchAttributeChange();
		}
	}

	@FXML
	private void changeLife() {
		// TODO!
		User user = null;
		PopOver popOver = new PopOver();
		popOver.setTitle("Ändere Leben/Mentale Gesundheit");
		Button saveButton = new Button("Speichern");
		Button cancelButton = new Button("Abbrechen");
		cancelButton.setOnAction(e -> {
			e.consume();
			popOver.hide();
		});

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField lifeTextField = new TextField();
		lifeTextField.setPromptText("Leben");
		lifeTextField.setText(Integer.toString(user.getCharacter().getLife()));
		TextField mentalHealthTextField = new TextField();
		mentalHealthTextField.setPromptText("Ment. Gesund.");
		mentalHealthTextField.setText(Integer.toString(user.getCharacter().getMentalHealth()));

		saveButton.setOnAction(e -> {
			e.consume();
			int life = Integer.parseInt(lifeTextField.getText());
			int mentalHealth = Integer.parseInt(mentalHealthTextField.getText());
			user.getCharacter().setLife(life);
			user.getCharacter().setMentalHealth(mentalHealth);
			getPresenter().updateUser();
			popOver.hide();
		});

		grid.add(new Label("Leben"), 0, 0);
		grid.add(lifeTextField, 1, 0);
		grid.add(new Label("Ment. Gesund.:"), 0, 1);
		grid.add(mentalHealthTextField, 1, 1);
		grid.add(saveButton, 0, 2);
		grid.add(cancelButton, 1, 2);

		saveButton.setDisable(mentalHealthTextField.getText().isEmpty() || lifeTextField.getText().isEmpty());

		lifeTextField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(mentalHealthTextField.getText().isEmpty() || lifeTextField.getText().isEmpty()));
		mentalHealthTextField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(mentalHealthTextField.getText().isEmpty() || lifeTextField.getText().isEmpty()));

		TextFieldListener.integerOnly(mentalHealthTextField);
		TextFieldListener.integerOnly(lifeTextField);

		popOver.setContentNode(grid);
		FXUtils.runOnApplicationThread(lifeTextField::requestFocus);
		popOver.setAutoHide(false);
		popOver.setDetached(true);
		popOver.show(getStage());
	}

	@FXML
	private void newValueEntry(ActionEvent event) {
		event.consume();

		PopOver popOver = new PopOver();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField nameTextField = new TextField();
		nameTextField.setPromptText("Name");
		TextField valueTextField = new TextField();
		valueTextField.setPromptText("Wert");
		ChoiceBox<String> typeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Attribut", "Fertigkeit"));
		typeChoiceBox.getSelectionModel().select(0);

		grid.add(new Label("Typ"), 0, 0);
		grid.add(typeChoiceBox, 1, 0);
		grid.add(new Label("Name:"), 0, 1);
		grid.add(nameTextField, 1, 1);
		grid.add(new Label("Wert:"), 0, 2);
		grid.add(valueTextField, 1, 2);

		Button saveButton = new Button("Speichern");
		saveButton.setOnMouseClicked(e -> {
			e.consume();
			// TODO
			User user = null;
			String selectedType = typeChoiceBox.getSelectionModel().getSelectedItem();
			String name = nameTextField.getText();
			int value = Integer.parseInt(valueTextField.getText());
			if (selectedType.equals("Attribut")) {
				user.getCharacter().getBaseValues().add(new BaseValue(name, value));
			} else {
				user.getCharacter().getAttributeList().add(new Attribute(name, value));
			}
			getPresenter().updateUser();
			getPresenter().updateAttributes();
			getPresenter().updateBaseValues();

			valueTextField.clear();
			nameTextField.clear();
			nameTextField.requestFocus();
		});

		Button cancelButton = new Button("Abbrechen");
		cancelButton.setOnMouseClicked(e -> {
			e.consume();
			popOver.hide(Duration.millis(500));
		});

		grid.add(saveButton, 0, 3);
		grid.add(cancelButton, 1, 3);

		saveButton.setDisable(true);

		nameTextField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(valueTextField.getText().isEmpty() || nameTextField.getText().isEmpty()));
		valueTextField.textProperty().addListener((observable, oldValue, newValue) -> saveButton.setDisable(valueTextField.getText().isEmpty() || nameTextField.getText().isEmpty()));
		TextFieldListener.integerOnly(valueTextField);
		FXUtils.runOnApplicationThread(nameTextField::requestFocus);

		popOver.setContentNode(grid);
		popOver.detach();
		popOver.setTitle("Neuer Wert");
		popOver.show(getStage());
	}

	@Override
	protected void configureCenter(SplitPane splitPane) {
		splitPane.getItems().add(attributeAndBaseValueView);
		splitPane.setDividerPosition(0, 0.9);
	}

	@Override
	public void setBaseValues(List<BaseValue> baseValues) {
		FXUtils.runOnApplicationThread(() -> {
			Logger.debug("Setting BaseValues: " + baseValues);
			baseValueListView.getItems().setAll(baseValues);
		});
	}

	@Override
	public void setAttributes(List<Attribute> attributes) {
		FXUtils.runOnApplicationThread(() -> {
			Logger.debug("Setting Attributes: " + attributes);
			attributeListView.getItems().setAll(attributes);
		});
	}

	private class BaseValueClickHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
				mouseEvent.consume();
				BaseValue selected = baseValueListView.getSelectionModel().getSelectedItem();
				if (selected == null) {
					return;
				}

				new ChangePopover(value -> {
					selected.setValue(Integer.parseInt(value));
					getPresenter().updateBaseValues();
					getPresenter().storeUser();
				}).construct(selected.getName(), String.valueOf(selected.getValue()))
						.setTitle("Ändere ein Attribute")
						.show(baseValueListView);
			}
		}
	}

	private class AttributeClickHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
				mouseEvent.consume();
				Attribute selected = attributeListView.getSelectionModel().getSelectedItem();
				if (selected == null) {
					return;
				}

				new ChangePopover(value -> {
					selected.setValue(Integer.parseInt(value));
					getPresenter().updateAttributes();
					getPresenter().storeUser();
				}).construct(selected.getName(), String.valueOf(selected.getValue()))
						.setTitle("Ändere eine Fertigkeit")
						.show(attributeListView);
			}
		}
	}

	private final class BaseValueListCell extends ListCell<BaseValue> {

		@Override
		public void updateItem(BaseValue value, boolean empty) {
			super.updateItem(value, empty);
			if (empty) {
				setGraphic(null);
				setContextMenu(null);
			} else {
				ContextMenu contextMenu = new ContextMenu();
				MenuItem changeMenuItem = new MenuItem("Ändern");
				changeMenuItem.setOnAction(event -> dispatchAttributeChange());
				MenuItem deleteMenuItem = new MenuItem("Löschen");
				deleteMenuItem.setOnAction(event -> getPresenter().removeBaseValue(value));
				contextMenu.getItems().addAll(changeMenuItem, deleteMenuItem);

				setContextMenu(contextMenu);
				setGraphic(new Label(value.toString()));
			}
		}
	}

	private final class AttributeListCell extends ListCell<Attribute> {

		@Override
		public void updateItem(Attribute value, boolean empty) {
			super.updateItem(value, empty);
			if (empty) {
				setGraphic(null);
				setContextMenu(null);
			} else {
				ContextMenu contextMenu = new ContextMenu();
				MenuItem changeMenuItem = new MenuItem("Ändern");
				changeMenuItem.setOnAction(event -> dispatchAttributeChange());
				MenuItem deleteMenuItem = new MenuItem("Löschen");
				deleteMenuItem.setOnAction(event -> getPresenter().removeAttribute(value));

				contextMenu.getItems().addAll(changeMenuItem, deleteMenuItem);

				setContextMenu(contextMenu);
				Node graphic = new HBox(new Label(value.getName()), new Label(" = "), new Label(Integer.toString(value.getValue())));
				HBox.setHgrow(graphic, Priority.ALWAYS);
				setGraphic(graphic);

			}
		}
	}

	private class PlayerEventHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			if (event.isControlDown()) {
				if (event.getCode() == KeyCode.Q) {
					event.consume();
					getPresenter().backToServerView();
				} else if (event.getCode() == KeyCode.L) {
					event.consume();
					// TODO
					new NotesListView(null).show();
				}
			}
		}
	}
}
