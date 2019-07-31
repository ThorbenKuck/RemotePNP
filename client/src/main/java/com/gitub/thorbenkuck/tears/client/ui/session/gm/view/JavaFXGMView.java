package com.gitub.thorbenkuck.tears.client.ui.session.gm.view;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.ui.*;
import com.gitub.thorbenkuck.tears.client.ui.session.AbstractSessionView;
import com.gitub.thorbenkuck.tears.client.ui.session.gm.presenter.GMPresenter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;

class JavaFXGMView extends AbstractSessionView<GMPresenter> implements GMView {

	private final ObservableList<User> userObservableList = FXCollections.observableArrayList();
	private final MapSoundHandler mapSoundHandler = new MapSoundHandler();
	private final ImageHandler imageHandler = new ImageHandler();
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
	@FXML
	private TextField rollAmount;
	private int drawingCount = 0;

	JavaFXGMView(GMPresenter gmPresenter) {
		super(gmPresenter);
		Logger.info("You are a GM");
	}

	@FXML
	private void privateDiceRoll(Event event) {
		event.consume();
		Logger.trace("Rolling a " + privateDiceInput.getText() + " sided dice privately");
		getPresenter().privateRoll(privateDiceInput.getText(), rollAmount.getText());
	}

	@FXML
	private void newDrawRequested(ActionEvent event) {
		System.out.println("new draw");
		event.consume();

		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Zeichnen");
		dialog.initOwner(getStage());
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
		byte[] bytes = imageHandler.readImageFile(getStage());
		displayImage(bytes);
	}

	private byte[] currentImageToByte() {
		if (centralTabPane.getSelectionModel().isEmpty()) {
			return null;
		}
		Pane stackPane = (Pane) centralTabPane.getSelectionModel().getSelectedItem().getContent();
		Node node = stackPane.getChildren().get(0);
		if (node != null && node.getClass().equals(ImageView.class)) {
			ImageView imageView = (ImageView) node;
			try {
				return imageHandler.imageToByteArray(imageView.getImage());
			} catch (IOException e) {
				appendChatMessage("Das Bild konnte nicht konvertiert werden");
			}
		} else if (node != null) {
			Image image = node.snapshot(new SnapshotParameters(), null);
			try {
				return imageHandler.imageToByteArray(image);
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
			getPresenter().sendToAll(res);
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
	private void sendImageToSpecific(Event event) {
		ChoiceDialog<User> alert = new ChoiceDialog<>();
		alert.getItems().addAll(userObservableList);
		alert.setTitle("Empfänger auswahl");
		alert.initOwner(getStage());
		alert.setHeaderText("Wähle die Person, die das Bild sehen soll");
		Optional<User> optional = alert.showAndWait();

		optional.ifPresent(user -> {
			byte[] res = currentImageToByte();
			if (res != null) {
				getPresenter().sendToSpecific(user, res);
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
		getPresenter().sendChatMessage(message);
	}

	@FXML
	private void publicDiceRoll(Event event) {
		event.consume();
		Logger.trace("Rolling a " + publicDiceInput.getText() + " sided dice publicly");
		getPresenter().publicRoll(publicDiceInput.getText(), rollAmount.getText());
	}

	@FXML
	private void mapLocal(ActionEvent event) {
		System.out.println("map local");
		event.consume();
		mapSoundHandler.apply(getStage()).ifPresent(getPresenter()::sendChatMessage);
	}

	@Override
	protected void configureCenter(SplitPane splitPane) {

	}

	@Override
	protected void configureDices(HBox diceRollRoot) {

	}

	@Override
	protected void configureListViews(HBox listViewRoot) {

	}

	@Override
	public void updateSoundBoard() {
		FXUtils.runOnApplicationThread(() -> soundBoardListView.getItems().setAll(SoundBoard.getInstance().createSoundList()));
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
			button2.setOnMouseClicked(event -> getPresenter().playOnAll(getItem()));
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
					getPresenter().backToServerView();
				} else if (event.getCode() == KeyCode.L) {
					new NotesListView(getPresenter().getUser()).show();
				}
			}
		}
	}
}
