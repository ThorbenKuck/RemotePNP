package com.gitub.thorbenkuck.tears.client.ui.player.view;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.media.MediaCenter;
import com.gitub.thorbenkuck.tears.client.ui.*;
import com.gitub.thorbenkuck.tears.client.ui.player.presenter.PlayerPresenter;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class JavaFXPlayerView implements PlayerView {

	private final ObservableList<User> userObservableList = FXCollections.observableArrayList();
	private final Repository repository;
	private PlayerPresenter playerPresenter;
	private Parent root;
	private Scene scene;
	private Stage stage;
	private int imageCount = 0;
	@FXML
	private ListView<User> participantsList;
	@FXML
	private ListView<BaseValue> baseValueListView;
	@FXML
	private ListView<Attribute> attributeListView;
	@FXML
	private Label publicDiceResult;
	@FXML
	private TextField publicDiceInput;
	@FXML
	private TextArea chatMessages;
	@FXML
	private TextField chatInput;
	@FXML
	private ListView<NotesInformation> notesListView;
	@FXML
	private TabPane centralTabPane;
	@FXML
	private HBox bottomBox;

	JavaFXPlayerView(PlayerPresenter playerPresenter, Repository repository) {
		this.playerPresenter = playerPresenter;
		this.repository = repository;
		Logger.info("You are a player");
	}

	@FXML
	private void selectPlayer(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			User user = participantsList.getSelectionModel().getSelectedItem();
			Tab dialog = new DetailPlayerTab(user, true);
			centralTabPane.getTabs().add(dialog);
			centralTabPane.getSelectionModel().select(dialog);
			centralTabPane.requestFocus();
		}
	}

	@FXML
	private void baseValueClickHandle(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			BaseValue selected = baseValueListView.getSelectionModel().getSelectedItem();
			if (selected == null) {
				return;
			}
			PopOver popOver = new PopOver();
			TextField textField = new TextField(Integer.toString(selected.getValue()));
			TextFieldListener.integerOnly(textField);
			PopOver error = new PopOver(new Label("Dieses Feld muss ausgefüllt sein!"));
			error.setDetachable(false);
			error.setAnimated(true);
			error.setOpacity(0.8);
			Button done = new Button("Fertig");
			Runnable r = () -> {
				if(textField.getText().isEmpty()) {
					if(error.isShowing()) {
						error.hide(Duration.millis(400));
					}
					error.show(textField);
				} else {
					selected.setValue(Integer.parseInt(textField.getText()));
					popOver.hide(Duration.millis(300));
					playerPresenter.updateAttributes();
					playerPresenter.storeUser();
				}
			};
			done.setOnMouseClicked(e -> {
				if(e.getButton() == MouseButton.PRIMARY) {
					e.consume();
					r.run();
				}
			});
			textField.setOnKeyPressed(e -> {
				if(e.getCode() == KeyCode.ENTER) {
					e.consume();
					r.run();
				} else if(e.getCode() == KeyCode.ESCAPE) {
					popOver.hide(Duration.millis(300));
				}
			});

			GridPane gridPane = new GridPane();
			gridPane.add(new Label(selected.getName()), 0, 0);
			gridPane.add(textField, 1, 0);
			gridPane.add(done, 1, 1);

			popOver.setTitle("Ändere Fertigkeit");
			popOver.setContentNode(gridPane);
			popOver.setDetachable(false);
			popOver.setAutoFix(false);
			popOver.setHideOnEscape(true);
			popOver.setCloseButtonEnabled(true);
			popOver.show(baseValueListView);

		}
	}

	private void dispatchAttributeChange() {
		Attribute selected = attributeListView.getSelectionModel().getSelectedItem();
		if(selected == null) {
			return;
		}
		PopOver popOver = new PopOver();
		TextField textField = new TextField(Integer.toString(selected.getValue()));
		TextFieldListener.integerOnly(textField);
		PopOver error = new PopOver(new Label("Dieses Feld muss ausgefüllt sein!"));
		error.setDetachable(false);
		error.setAnimated(true);
		error.setOpacity(0.8);
		Button done = new Button("Fertig");
		Runnable r = () -> {
			if(textField.getText().isEmpty()) {
				if(error.isShowing()) {
					error.hide(Duration.millis(400));
				}
				error.show(textField);
			} else {
				selected.setValue(Integer.parseInt(textField.getText()));
				popOver.hide(Duration.millis(300));
				playerPresenter.updateAttributes();
				playerPresenter.storeUser();
			}
		};
		done.setOnMouseClicked(e -> {
			if(e.getButton() == MouseButton.PRIMARY) {
				e.consume();
				r.run();
			}
		});
		textField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				e.consume();
				r.run();
			} else if(e.getCode() == KeyCode.ESCAPE) {
				popOver.hide(Duration.millis(300));
			}
		});

		GridPane gridPane = new GridPane();
		gridPane.add(new Label(selected.getName()), 0, 0);
		gridPane.add(textField, 1, 0);
		gridPane.add(done, 1, 1);

		popOver.setTitle("Ändere Fertigkeit");
		popOver.setContentNode(gridPane);
		popOver.setDetachable(false);
		popOver.setAutoFix(false);
		popOver.setHideOnEscape(true);
		popOver.setCloseButtonEnabled(true);
		popOver.show(attributeListView);

//		Optional<String> result = dialog.showAndWait();
//		if (result.isPresent()) {
//			try {
//				int i = Integer.parseInt(result.get());
//				selected.setValue(i);
//			} catch (NumberFormatException e) {
//				appendChatMessage("Der eingegebene Wert war keine Zahl!");
//				return;
//			}
//			playerPresenter.updateAttributes();
//			playerPresenter.storeUser();
//		}
	}

	@FXML
	private void attributeClickHandle(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			dispatchAttributeChange();
		}
	}

	@FXML
	private void keyPressedOnPublicDiceRoll(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			publicDiceRoll(event);
		}
	}

	@FXML
	private void actionInChatInput(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			sendChatMessage(event);
		}
	}

	@FXML
	private void sendChatMessage(Event event) {
		event.consume();
		String message = chatInput.getText();
		chatInput.clear();
		playerPresenter.dispatchChatMessage(message);
	}

	@FXML
	private void publicDiceRoll(Event event) {
		event.consume();
		Logger.trace("Rolling a " + publicDiceInput.getText() + " sided dice publicly");
		playerPresenter.publicRoll(publicDiceInput.getText());
	}

	// reset to the top left:
	private void reset(ImageView imageView, double width, double height) {
		imageView.setViewport(new Rectangle2D(0, 0, width, height));
	}

	// shift the viewport of the imageView by the specified delta, clamping so
	// the viewport does not move off the actual image:
	private void shift(ImageView imageView, Point2D delta) {
		Rectangle2D viewport = imageView.getViewport();

		double width = imageView.getImage().getWidth();
		double height = imageView.getImage().getHeight();

		double maxX = width - viewport.getWidth();
		double maxY = height - viewport.getHeight();

		double minX = clamp(viewport.getMinX() - delta.getX(), 0, maxX);
		double minY = clamp(viewport.getMinY() - delta.getY(), 0, maxY);

		imageView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
	}

	private double clamp(double value, double min, double max) {

		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	// convert mouse coordinates in the imageView to coordinates in the actual image:
	private Point2D imageViewToImage(ImageView imageView, Point2D imageViewCoordinates) {
		double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
		double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

		Rectangle2D viewport = imageView.getViewport();
		return new Point2D(
				viewport.getMinX() + xProportion * viewport.getWidth(),
				viewport.getMinY() + yProportion * viewport.getHeight());
	}

	@Override
	public void tryAcceptDownload(String contextText, String name, Consumer<Boolean> callback) {
		FXUtils.runOnApplicationThread(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Download");
			alert.initOwner(stage);
			alert.setHeaderText("Name: " + name);
			alert.setContentText(contextText);

			Optional<ButtonType> result = alert.showAndWait();
			result.ifPresent(type -> {
				if (type == ButtonType.OK) {
					callback.accept(true);
				} else {
					callback.accept(false);
				}
			});
		});
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

	@Override
	public void setParticipantsList(List<User> participantsList) {
		List<User> sublist = participantsList.stream()
				.filter(user -> !user.getUserName().equals(repository.get(User.class).getUserName()))
				.collect(Collectors.toList());

		FXUtils.runOnApplicationThread(() -> {
			userObservableList.clear();
			userObservableList.setAll(sublist);
		});
	}

	@Override
	public PlayerPresenter getPresenter() {
		return playerPresenter;
	}

	@Override
	public void setTitle(String userName) {
		FXUtils.runOnApplicationThread(() -> stage.setTitle(userName));
	}

	@Override
	public void appendChatMessage(String message) {
		FXUtils.runOnApplicationThread(() -> chatMessages.appendText(message + "\n"));
	}

	@Override
	public void setPublicDiceResult(int result) {
		if (result < 0) {
			FXUtils.runOnApplicationThread(() -> {
				publicDiceInput.clear();
				publicDiceInput.requestFocus();
			});
		} else {
			FXUtils.runOnApplicationThread(() -> publicDiceResult.setText(Integer.toString(result)));
		}
	}

	@Override
	public void setNotes(List<NotesInformation> informationList) {
		notesListView.getItems().setAll(informationList);
	}

	@Override
	public File requestStorePath() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Character files (*.character)", "*.character");
		fileChooser.getExtensionFilters().add(extFilter);
		return fileChooser.showSaveDialog(stage);
	}

	@Override
	public void clearChatLog() {
		FXUtils.runOnApplicationThread(() -> chatMessages.clear());
	}

	@Override
	public void displayImage(byte[] data) {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
			BufferedImage bufferedImage = ImageIO.read(inputStream);
			WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
			ImageView imageView = new ImageView(writableImage);

			double width = writableImage.getWidth();
			double height = writableImage.getHeight();

			imageView.setPreserveRatio(true);
			final int MIN_PIXELS = 10;

			reset(imageView, width, height);

			ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();

			imageView.setOnMousePressed(e -> {
				Point2D mousePress = imageViewToImage(imageView, new Point2D(e.getX(), e.getY()));
				mouseDown.set(mousePress);
			});

			imageView.setOnMouseDragged(e -> {
				Point2D dragPoint = imageViewToImage(imageView, new Point2D(e.getX(), e.getY()));
				shift(imageView, dragPoint.subtract(mouseDown.get()));
				mouseDown.set(imageViewToImage(imageView, new Point2D(e.getX(), e.getY())));
			});

			imageView.setOnScroll(e -> {
				double delta = e.getDeltaY();
				Rectangle2D viewport = imageView.getViewport();

				double scale = clamp(Math.pow(1.01, delta),

						// don't scale so we're zoomed in to fewer than MIN_PIXELS in any direction:
						Math.min(MIN_PIXELS / viewport.getWidth(), MIN_PIXELS / viewport.getHeight()),

						// don't scale so that we're bigger than image dimensions:
						Math.max(width / viewport.getWidth(), height / viewport.getHeight())

				);

				Point2D mouse = imageViewToImage(imageView, new Point2D(e.getX(), e.getY()));

				double newWidth = viewport.getWidth() * scale;
				double newHeight = viewport.getHeight() * scale;

				// To keep the visual point under the mouse from moving, we need
				// (x - newViewportMinX) / (x - currentViewportMinX) = scale
				// where x is the mouse X coordinate in the image

				// solving this for newViewportMinX gives

				// newViewportMinX = x - (x - currentViewportMinX) * scale

				// we then clamp this value so the image never scrolls out
				// of the imageview:

				double newMinX = clamp(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale,
						0, width - newWidth);
				double newMinY = clamp(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale,
						0, height - newHeight);

				imageView.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
			});

			imageView.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					reset(imageView, width, height);
				}
			});

			StackPane stackPane = new StackPane(imageView);
			imageView.fitWidthProperty().bind(centralTabPane.widthProperty());
			imageView.fitHeightProperty().bind(centralTabPane.heightProperty().subtract(40));

			Tab tab = new Tab(Integer.toString(imageCount++), stackPane);

			FXUtils.runOnApplicationThread(() -> {
				centralTabPane.getTabs().add(tab);
				centralTabPane.getSelectionModel().select(tab);
			});
		} catch (IOException e) {
			Logger.catching(e);
		}
	}

	@Override
	public void focus() {
		FXUtils.runOnApplicationThread(() -> stage.requestFocus());
	}

	@Override
	public void close() {
		playerPresenter = null;
		stage = null;
		root = null;
		scene = null;
	}

	@Override
	public void setup() {
		URL location = SetupView.class.getClassLoader().getResource("player_view.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(location);
		fxmlLoader.setController(this);

		Parent parent;
		try {
			parent = fxmlLoader.load();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		FXUtils.runOnApplicationThread(() -> {
			root = parent;
			scene = new Scene(root);
			centralTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
			scene.setOnKeyPressed(new PlayerEventHandler());

			attributeListView.setCellFactory(lv -> new AttributeListCell());
			attributeListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue) {
					attributeListView.getSelectionModel().clearSelection();
				}
			});

			baseValueListView.setCellFactory(lv -> new BaseValueListCell());
			baseValueListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue) {
					baseValueListView.getSelectionModel().clearSelection();
				}
			});
		});
	}

	@Override
	public void display() {
		FXUtils.runOnApplicationThread(() -> {
			stage.setScene(scene);
			root.getStylesheets().clear();
			root.getStylesheets().add(StyleSheetCache.pathToPlayerView());
			stage.setResizable(true);
			stage.setMaximized(true);
			stage.show();

			participantsList.setItems(userObservableList);
			participantsList.setCellFactory(new ParticipantCellFactory(stage, this::appendChatMessage, playerPresenter::store, playerPresenter::dispatchChatMessage));
		});
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
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
				deleteMenuItem.setOnAction(event -> playerPresenter.removeBaseValue(value));
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
				deleteMenuItem.setOnAction(event -> playerPresenter.removeAttribute(value));

				contextMenu.getItems().addAll(changeMenuItem, deleteMenuItem);
				;
				setContextMenu(contextMenu);
				Node graphic = new HBox(new Label(value.getName()), new Label(" = "), new Label(Integer.toString(value.getValue())));
				HBox.setHgrow(graphic, Priority.ALWAYS);
				setGraphic(graphic);

			}
		}
	}

	@FXML
	private void changeLife() {
		User user = repository.get(User.class);
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
			playerPresenter.updateUser();
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
		popOver.show(stage);
	}

	private final MapSoundHandler mapSoundHandler = new MapSoundHandler();

	@FXML
	private void mapLocal(ActionEvent event) {
		event.consume();
		mapSoundHandler.apply(stage).ifPresent(playerPresenter::dispatchChatMessage);
	}

	@FXML
	private void editNotes(ActionEvent event) {
		event.consume();
		ChoiceDialog<NotesInformation> choiceDialog = new ChoiceDialog<>(null, notesListView.getItems());
		choiceDialog.setTitle("Notizen Auswahl");
		choiceDialog.setHeaderText("Welche Notizen möchtest du bearbeiten?");
		choiceDialog.setContentText("Ausgewählte Noitzen");

		Optional<NotesInformation> optionalNotesInformation = choiceDialog.showAndWait();
		optionalNotesInformation.ifPresent(this::editSpecificNotes);
	}

	@FXML
	private void actionInNotesView(MouseEvent mouseEvent) {
		if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			NotesInformation i = notesListView.getSelectionModel().getSelectedItem();

			if(i != null) {
				mouseEvent.consume();
				editSpecificNotes(i);
			}
		}
	}

	@FXML
	private void editSpecificNotes(NotesInformation notesInformation) {
		playerPresenter.editNotes(notesInformation);
	}

	@FXML
	private void saveCharacter(ActionEvent event) {
		event.consume();
		playerPresenter.storeUser();
		playerPresenter.updateAttributes();
		playerPresenter.updateBaseValues();
	}

	@FXML
	private void createNewNotes(ActionEvent event) {
		event.consume();
		PopOver popOver = new PopOver();
		TextField textField = new TextField();
		Button create = new Button("Erstelle");
		Button cancel = new Button("Abbrechen");
		GridPane gridPane = new GridPane();

		textField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				e.consume();
				if(!textField.getText().isEmpty()) {
					String name = textField.getText();
					playerPresenter.createNewNotes(name);
				}

				popOver.hide();
			}
		});
		create.setOnAction(e -> {
			e.consume();
			if(!textField.getText().isEmpty()) {
				String name = textField.getText();
				playerPresenter.createNewNotes(name);
			}

			popOver.hide();
		});
		cancel.setOnAction(e -> {
			e.consume();
			popOver.hide();
		});


		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.add(new Label("Wie Soll die neue Notiz heißen?"), 0, 0);
		gridPane.add(new Label("Name"), 0, 1);
		gridPane.add(textField, 1, 1);
		gridPane.add(new HBox(create, cancel), 1, 2);

		popOver.setDetached(true);
		popOver.setCloseButtonEnabled(true);
		popOver.setContentNode(gridPane);
		popOver.show(stage);
	}

	@FXML
	private void newValueEntry(ActionEvent event) {
		event.consume();

//		Dialog<Pair<String, Pair<String, String>>> dialog = new Dialog<>();
//		dialog.setTitle("Neuer Wert");
//		dialog.initOwner(stage);
//		dialog.setHeaderText("Füge ein neues Attribut/eine neue Fertigkeit zu deinem Charakter hinzu");
//		ButtonType loginButtonType = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
//		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
//		GridPane grid = new GridPane();
//		grid.setHgap(10);
//		grid.setVgap(10);
//		grid.setPadding(new Insets(20, 150, 10, 10));
//
//		TextField nameTextField = new TextField();
//		nameTextField.setPromptText("Name");
//		TextField valueTextField = new TextField();
//		valueTextField.setPromptText("Wert");
//		ChoiceBox<String> typeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Attribut", "Fertigkeit"));
//		typeChoiceBox.getSelectionModel().select(0);
//
//		grid.add(new Label("Typ"), 0, 0);
//		grid.add(typeChoiceBox, 1, 0);
//		grid.add(new Label("Name:"), 0, 1);
//		grid.add(nameTextField, 1, 1);
//		grid.add(new Label("Wert:"), 0, 2);
//		grid.add(valueTextField, 1, 2);
//		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
//		loginButton.setDisable(true);
//
//		nameTextField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(valueTextField.getText().isEmpty() || nameTextField.getText().isEmpty()));
//		valueTextField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(valueTextField.getText().isEmpty() || nameTextField.getText().isEmpty()));
//		TextFieldListener.integerOnly(valueTextField);
//		dialog.getDialogPane().setContent(grid);
//		FXUtils.runOnApplicationThread(nameTextField::requestFocus);
//
//		dialog.setResultConverter(dialogButton -> {
//			if (dialogButton == loginButtonType) {
//				return new Pair<>(typeChoiceBox.getSelectionModel().getSelectedItem(), new Pair<>(nameTextField.getText(), valueTextField.getText()));
//			}
//			return null;
//		});
//
//		Optional<Pair<String, Pair<String, String>>> result = dialog.showAndWait();
//
//		result.ifPresent(output -> {
//			User user = repository.get(User.class);
//			String selectedType = output.getKey();
//			String name = output.getValue().getKey();
//			int value = Integer.parseInt(output.getValue().getValue());
//			if (selectedType.equals("Attribut")) {
//				user.getCharacter().getBaseValues().add(new BaseValue(name, value));
//			} else {
//				user.getCharacter().getAttributeList().add(new Attribute(name, value));
//			}
//			playerPresenter.updateUser();
//			playerPresenter.updateAttributes();
//			playerPresenter.updateBaseValues();
//		});

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
			User user = repository.get(User.class);
			String selectedType = typeChoiceBox.getSelectionModel().getSelectedItem();
			String name = nameTextField.getText();
			int value = Integer.parseInt(valueTextField.getText());
			if (selectedType.equals("Attribut")) {
				user.getCharacter().getBaseValues().add(new BaseValue(name, value));
			} else {
				user.getCharacter().getAttributeList().add(new Attribute(name, value));
			}
			playerPresenter.updateUser();
			playerPresenter.updateAttributes();
			playerPresenter.updateBaseValues();

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
		popOver.show(stage);
	}

	@FXML
	private void mute() {
		MediaCenter.changeMute();
		if (MediaCenter.isMuted()) {
			appendChatMessage("Sound deactivated");
		} else {
			appendChatMessage("Sound activated");
		}
	}

	@FXML
	private void higherVolume() {
		if(MediaCenter.higherVolume()) {
			appendChatMessage("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
		}
	}

	@FXML
	private void lowerVolume() {
		if(MediaCenter.lowerVolume()) {
			appendChatMessage("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
		}
	}

	@FXML
	private void killSounds() {
		appendChatMessage("Musik wiedergabe beendet");
		playerPresenter.killAllSounds();
	}

	private class PlayerEventHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			if (event.isControlDown()) {
				if (event.getCode() == KeyCode.Q) {
					event.consume();
					playerPresenter.backToServerView();
				} else if(event.getCode() == KeyCode.L) {
					event.consume();
					new NotesListView(repository.get(User.class)).show();
				}
			}
		}
	}
}
