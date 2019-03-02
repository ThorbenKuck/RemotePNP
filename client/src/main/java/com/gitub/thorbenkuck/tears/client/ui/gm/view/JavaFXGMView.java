package com.gitub.thorbenkuck.tears.client.ui.gm.view;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.media.MediaCenter;
import com.gitub.thorbenkuck.tears.client.ui.*;
import com.gitub.thorbenkuck.tears.client.ui.gm.presenter.GMPresenter;
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
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;

class JavaFXGMView implements GMView {

	private final ObservableList<User> userObservableList = FXCollections.observableArrayList();
	private final MapSoundHandler mapSoundHandler = new MapSoundHandler();
	private GMPresenter gmPresenter;
	private Parent root;
	private Scene scene;
	private Stage stage;
	@FXML
	private Label privateDiceResult;
	@FXML
	private TextField privateDiceInput;
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
	private ListView<User> participantsList;
	@FXML
	private ListView<Sound> soundBoardListView;
	@FXML
	private TabPane centralTabPane;
	private int drawingCount = 0;

	JavaFXGMView(GMPresenter gmPresenter) {
		this.gmPresenter = gmPresenter;
		Logger.info("You are a GM");
	}

	@FXML
	private void privateDiceRoll(Event event) {
		event.consume();
		Logger.trace("Rolling a " + privateDiceInput.getText() + " sided dice privately");
		gmPresenter.privateRoll(privateDiceInput.getText());
	}

	@FXML
	private void keyPressedOnPrivateDiceRoll(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			privateDiceRoll(event);
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
	private void sendSoundToSpecific(ActionEvent actionEvent) {
		System.out.println("sending sound to specific");
		actionEvent.consume();

		ChoiceDialog<User> alert = new ChoiceDialog<>();
		alert.getItems().addAll(userObservableList);
		alert.setTitle("Empfänger Auswahl");
		alert.setHeaderText("Wähle die Person, die den Sound erhalten");
		alert.initOwner(stage);
		Optional<User> optional = alert.showAndWait();

		optional.ifPresent(user -> {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Sound name");
			dialog.setHeaderText("Welchen Namen soll die Sounddatei habe?");
			dialog.setContentText("Bitte gebe einen Namen ein, unter dem diese Sounddatei an alle gesendet werden soll");
			dialog.initOwner(stage);
			Logger.debug("Selected user " + user);
			dialog.showAndWait().ifPresent(name -> {
				while (name.contains(":")) {
					dialog.setHeaderText("Der Name darf kein : enthalten!");
					Optional<String> nameOptional = dialog.showAndWait();
					if (!nameOptional.isPresent()) {
						return;
					}
					name = nameOptional.get();
				}
				final String choosen = name;
				FileSelection.selectSoundFileToOpen(stage).ifPresent(file -> gmPresenter.requestSendingOf(file, user, choosen));
			});
		});
	}

	@FXML
	private void newDrawRequested(ActionEvent event) {
		System.out.println("new draw");
		event.consume();

		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Zeichnen");
		dialog.initOwner(stage);
		dialog.setHeaderText("Gebe die Dimensionen deiner Zeichnung an");

		ButtonType submitButtonType = new ButtonType("Okay!", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField widthTextField = new TextField();
		widthTextField.setPromptText("Width");
		TextField heightTextField = new TextField();
		heightTextField.setPromptText("Height");

		grid.add(new Label("Breite:"), 0, 0);
		grid.add(widthTextField, 1, 0);
		grid.add(new Label("Höhe:"), 0, 1);
		grid.add(heightTextField, 1, 1);

// Enable/Disable login button depending on whether a username was entered.
		Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
		submitButton.setDisable(true);

		TextFieldListener.integerOnly(widthTextField);
		TextFieldListener.integerOnly(heightTextField);

		widthTextField.textProperty().addListener((observable, oldValue, newValue) -> submitButton.setDisable(heightTextField.getText().isEmpty() || widthTextField.getText().isEmpty()));

		heightTextField.textProperty().addListener((observable, oldValue, newValue) -> submitButton.setDisable(heightTextField.getText().isEmpty() || widthTextField.getText().isEmpty()));

		dialog.getDialogPane().setContent(grid);
		FXUtils.runOnApplicationThread(widthTextField::requestFocus);
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == submitButtonType) {
				return new Pair<>(widthTextField.getText(), heightTextField.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();
		result.ifPresent(widthHeight -> {
			int width = Integer.parseInt(widthHeight.getKey());
			int height = Integer.parseInt(widthHeight.getValue());
			Canvas canvas = new Canvas(width, height);
			final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
			final ColorPicker colorPicker = new ColorPicker(Color.BLACK);
			final Slider slider = new Slider(1, 100, 1);
			final Label label = new Label("Dicke");
			final Label output = new Label("1");
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
			slider.valueProperty().addListener((observable, oldValue, newValue) -> {
				output.setText(Integer.toString(newValue.intValue()));
			});
			initDraw(graphicsContext);

			canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
					event1 -> {
						slider.setVisible(false);
						colorPicker.setVisible(false);
						label.setVisible(false);
						graphicsContext.setLineWidth(slider.getValue());
						if (event1.getButton() == MouseButton.SECONDARY) {
							graphicsContext.setStroke(Color.LIGHTGRAY);
						} else {
							graphicsContext.setStroke(colorPicker.getValue());
						}
						graphicsContext.beginPath();
						graphicsContext.moveTo(event1.getX(), event1.getY());
						graphicsContext.stroke();
					});

			canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
					event12 -> {
						graphicsContext.lineTo(event12.getX(), event12.getY());
						graphicsContext.stroke();
					});

			canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
					event13 -> {
						slider.setVisible(true);
						colorPicker.setVisible(true);
						label.setVisible(true);
					});

			canvas.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
				if (keyEvent.getCode() == KeyCode.ESCAPE) {
					graphicsContext.clearRect(0, 0, width, height);
				}
			});

			FlowPane root = new FlowPane();
			root.getChildren().add(canvas);
			root.getChildren().add(new VBox(colorPicker, new HBox(label, slider, output)));

			centralTabPane.getTabs().add(new Tab("Drawing " + drawingCount++, root));
		});
	}

	private void initDraw(GraphicsContext gc) {
		double canvasWidth = gc.getCanvas().getWidth();
		double canvasHeight = gc.getCanvas().getHeight();

		gc.setFill(Color.LIGHTGRAY);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(5);

		gc.fill();
		gc.strokeRect(
				0,              //x of the upper left corner
				0,              //y of the upper left corner
				canvasWidth,    //width of the rectangle
				canvasHeight);  //height of the rectangle

		gc.setLineWidth(1);

	}

	@FXML
	private void openNewImage(Event event) {
		System.out.println("open image");
		event.consume();

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
		fileChooser.getExtensionFilters().add(extFilter);

		File selected = fileChooser.showOpenDialog(stage);
		try {
			Image image = new Image(new FileInputStream(selected));
			ImageView imageView = new ImageView(image);

			double width = image.getWidth();
			double height = image.getHeight();

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

			Tab tab = new Tab(selected.getName(), stackPane);

			FXUtils.runOnApplicationThread(() -> {
				centralTabPane.getTabs().add(tab);
				centralTabPane.getSelectionModel().select(tab);
				centralTabPane.requestFocus();
			});
		} catch (FileNotFoundException e) {
			appendChatMessage("Das Bild " + selected + " konnte nicht geöffnet werden!");
		}
	}

	private byte[] currentImageToByte() {
		if (centralTabPane.getSelectionModel().isEmpty()) {
			return null;
		}
		Pane stackPane = (Pane) centralTabPane.getSelectionModel().getSelectedItem().getContent();
		Node node = stackPane.getChildren().get(0);
		if (node != null && node.getClass().equals(ImageView.class)) {
			ImageView imageView = (ImageView) node;
			BufferedImage bImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				ImageIO.write(bImage, "jpg", outputStream);
				return outputStream.toByteArray();
			} catch (IOException e) {
				appendChatMessage("Das Bild konnte nicht konvertiert werden");
			}
		} else if (node != null) {
			Image image = node.snapshot(new SnapshotParameters(), null);
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				ImageIO.write(bufferedImage, "jpg", outputStream);
				return outputStream.toByteArray();
			} catch (IOException e) {
				appendChatMessage("Das Bild konnte nicht konvertiert werden");
			}
		}

		return null;
	}

	@FXML
	private void sendImageToAll(Event event) {
		byte[] res = currentImageToByte();
		if (res != null) {
			gmPresenter.sendToAll(res);
		} else {
			appendChatMessage("Kein Bild ausgewählt!");
		}
	}

	@FXML
	private void gmSelectPlayer(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			User user = participantsList.getSelectionModel().getSelectedItem();
			Tab dialog = new DetailPlayerTab(user, false);
			centralTabPane.getTabs().add(dialog);
			centralTabPane.getSelectionModel().select(dialog);
			centralTabPane.requestFocus();
		}
	}

	@FXML
	private void sendSoundToAll(Event event) {
		System.out.println("send sound to all");
		event.consume();

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Sound name");
		dialog.initOwner(stage);
		dialog.setHeaderText("Welchen Namen soll die Sounddatei habe?");
		dialog.setContentText("Bitte gebe einen Namen ein, unterdem diese Sounddatei an alle gesendet werden soll");
		dialog.showAndWait().ifPresent(name -> {
			while (name.contains(":")) {
				dialog.setHeaderText("Der Name darf kein \":\" enthalten!");
				Optional<String> nameOptional = dialog.showAndWait();
				if (!nameOptional.isPresent()) {
					return;
				}
				name = nameOptional.get();
			}
			final String choosen = name;
			FileSelection.selectSoundFileToOpen(stage).ifPresent(file -> gmPresenter.requestSendingOf(file, choosen));
		});
	}

	@FXML
	private void sendImageToSpecific(Event event) {
		ChoiceDialog<User> alert = new ChoiceDialog<>();
		alert.getItems().addAll(userObservableList);
		alert.setTitle("Empfänger auswahl");
		alert.initOwner(stage);
		alert.setHeaderText("Wähle die Person, die das Bild sehen soll");
		Optional<User> optional = alert.showAndWait();

		optional.ifPresent(user -> {
			byte[] res = currentImageToByte();
			if (res != null) {
				gmPresenter.sendToSpecific(user, res);
			} else {
				appendChatMessage("Kein Bild ausgewählt!");
			}
		});
	}

	@FXML
	private void sendChatMessage(Event event) {
		System.out.println("send chat message");
		event.consume();
		String message = chatInput.getText();
		chatInput.clear();
		gmPresenter.dispatchChatMessage(message);
	}

	@FXML
	private void publicDiceRoll(Event event) {
		event.consume();
		Logger.trace("Rolling a " + publicDiceInput.getText() + " sided dice publicly");
		gmPresenter.publicRoll(publicDiceInput.getText());
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

	@FXML
	private void mapLocal(ActionEvent event) {
		System.out.println("map local");
		event.consume();
		mapSoundHandler.apply(stage).ifPresent(gmPresenter::dispatchChatMessage);
	}

	@FXML
	private void editNotes(ActionEvent event) {
		System.out.println("edit notes");
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
				System.out.println("action in notes view");
				mouseEvent.consume();
				editSpecificNotes(i);
			}
		}
	}

	@FXML
	private void editSpecificNotes(NotesInformation notesInformation) {
		gmPresenter.editNotes(notesInformation);
	}

	@FXML
	private void createNewNotes(ActionEvent event) {
		event.consume();
		PopOver popOver = new PopOver();

		TextField textField = new TextField();
		textField.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				e.consume();
				if(!textField.getText().isEmpty()) {
					String name = textField.getText();
					gmPresenter.createNewNotes(name);
				}

				popOver.hide();
			}
		});
		Button create = new Button("Erstelle");
		create.setOnAction(e -> {
			e.consume();
			if(!textField.getText().isEmpty()) {
				String name = textField.getText();
				gmPresenter.createNewNotes(name);
			}

			popOver.hide();
		});
		Button cancel = new Button("Abbrechen");
		cancel.setOnAction(e -> {
			e.consume();
			popOver.hide();
		});


		GridPane gridPane = new GridPane();
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
	private void saveCharacter(ActionEvent event) {
		System.out.println("save character");
		event.consume();
		gmPresenter.storeUser();
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
		if (MediaCenter.higherVolume()) {
			appendChatMessage("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
		}
	}

	@FXML
	private void lowerVolume() {
		if (MediaCenter.lowerVolume()) {
			appendChatMessage("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
		}
	}

	@FXML
	private void killSounds() {
		appendChatMessage("Musik wiedergabe beendet");
		gmPresenter.killAllSounds();
	}

	@Override
	public void updateSoundBoard() {
		FXUtils.runOnApplicationThread(() -> soundBoardListView.getItems().setAll(SoundBoard.getInstance().createSoundList()));
	}

	@Override
	public GMPresenter getPresenter() {
		return gmPresenter;
	}

	@Override
	public void appendChatMessage(String message) {
		FXUtils.runOnApplicationThread(() -> chatMessages.appendText(message + "\n"));
	}

	@Override
	public void setPrivateDiceResult(int result) {
		if (result < 0) {
			FXUtils.runOnApplicationThread(() -> {
				privateDiceInput.clear();
				privateDiceInput.requestFocus();
			});
		} else {
			FXUtils.runOnApplicationThread(() -> privateDiceResult.setText(Integer.toString(result)));
		}
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
	public void clearChatLog() {
		FXUtils.runOnApplicationThread(() -> chatMessages.clear());
	}

	@Override
	public File requestStorePath() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Character files (*.character)", "*.character");
		fileChooser.getExtensionFilters().add(extFilter);
		return fileChooser.showSaveDialog(stage);
	}

	@Override
	public void setNotes(List<NotesInformation> informationList) {
		notesListView.getItems().setAll(informationList);
	}

	@Override
	public void setTitle(String userName) {
		FXUtils.runOnApplicationThread(() -> stage.setTitle(userName));
	}

	@Override
	public void setParticipantsList(List<User> participants) {
		FXUtils.runOnApplicationThread(() -> {
			userObservableList.clear();
			userObservableList.setAll(participants);
		});
	}

	@Override
	public void focus() {
		FXUtils.runOnApplicationThread(() -> stage.requestFocus());
	}

	@Override
	public void close() {
		gmPresenter = null;
		stage = null;
		root = null;
		scene = null;
	}

	@Override
	public void setup() {
		URL location = SetupView.class.getClassLoader().getResource("gm_view.fxml");
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
			scene.setOnKeyPressed(new GMEventHandler());
			participantsList.setItems(userObservableList);
			updateSoundBoard();
			soundBoardListView.setCellFactory(lv -> new SoundBoardCell());
			participantsList.setCellFactory(new ParticipantCellFactory(stage, this::appendChatMessage, gmPresenter::store, gmPresenter::dispatchChatMessage));
		});
	}

	@Override
	public void display() {
		FXUtils.runOnApplicationThread(() -> {
			stage.setScene(scene);
			root.getStylesheets().clear();
			root.getStylesheets().add(StyleSheetCache.pathToGMView());
			stage.setResizable(true);
			stage.setMaximized(true);
			stage.show();
		});
	}

	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private class SoundBoardCell extends ListCell<Sound> {

		HBox hbox = new HBox();
		Label label = new Label("(empty)");
		Pane pane = new Pane();
		Button button = new Button("Test");
		Button button2 = new Button("Play");

		public SoundBoardCell() {
			super();
			hbox.getChildren().addAll(label, pane, button, button2);
			HBox.setHgrow(pane, Priority.ALWAYS);
			button.setOnMouseClicked(event -> SoundBoard.getInstance().playSound(getItem()));
			button2.setOnMouseClicked(event -> gmPresenter.playOnAll(getItem()));
		}

		@Override
		protected void updateItem(Sound item, boolean empty) {
			super.updateItem(item, empty);
			setText(null);  // No text in label of super class
			if (empty) {
				setGraphic(null);
			} else {
				label.setText(item != null ? item.getName() : "<null>");
				setGraphic(hbox);
			}
		}
	}

	private class GMEventHandler implements EventHandler<KeyEvent> {

		@Override
		public void handle(KeyEvent event) {
			if (event.isControlDown()) {
				if (event.getCode() == KeyCode.Q) {
					System.out.println("quit session");
					event.consume();
					gmPresenter.backToServerView();
				} else if (event.getCode() == KeyCode.L){
					new NotesListView(gmPresenter.getUser()).show();
				}
			}
		}
	}
}
