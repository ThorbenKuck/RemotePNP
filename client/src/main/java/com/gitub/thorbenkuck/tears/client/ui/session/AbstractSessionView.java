package com.gitub.thorbenkuck.tears.client.ui.session;

import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.gitub.thorbenkuck.tears.client.ui.*;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
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
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

public abstract class AbstractSessionView<T extends UserPresenter> implements SessionView {

	// TODO Auslagern
	private final MapSoundHandler mapSoundHandler = new MapSoundHandler();
	private T presenter;
	private Parent root;
	private Scene scene;
	private Stage stage;
	@FXML
	private ListView<User> participantsList;
	@FXML
	private ListView<Message> chatListView;
	@FXML
	private TextField chatInput;
	@FXML
	private HBox diceRollRoot;
	@FXML
	private HBox listViewRoot;
	@FXML
	private TextField rollAmount;
	@FXML
	private TextField publicDiceInput;
	@FXML
	private TabPane centralTabPane;
	@FXML
	private SplitPane centralSplitPane;
	@FXML
	private ListView<NotesInformation> notesListView;
	@FXML
	private Label publicDiceResult;
	private int imageCount = 0;

	public AbstractSessionView(T presenter) {
		this.presenter = presenter;
	}

	private void configure() {
		configureCenter(centralSplitPane);
		configureDices(diceRollRoot);
		configureListViews(listViewRoot);
	}

	private void makeCharacterDownload(Character character, String filePath) {
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
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
			NotesInformation i = notesListView.getSelectionModel().getSelectedItem();

			if (i != null) {
				mouseEvent.consume();
				editSpecificNotes(i);
			}
		}
	}

	@FXML
	private void editSpecificNotes(NotesInformation notesInformation) {
		getPresenter().editNotes(notesInformation);
	}

	@FXML
	private void lowerVolume(Event e) {
		presenter.lowerVolume();
	}

	@FXML
	private void higherVolume(Event e) {
		presenter.higherVolume();
	}

	@FXML
	private void mute(Event e) {
		presenter.mute();
	}

	@FXML
	private void killSounds(Event e) {
		appendChatMessage("Musik wiedergabe beendet");
		presenter.killAllSounds();
	}

	@FXML
	private void mapLocal(Event e) {
		e.consume();
		mapSoundHandler.apply(getStage()).ifPresent(getPresenter()::sendChatMessage);
	}

	@FXML
	private void createNewNotes(Event event) {
		event.consume();
		PopOver popOver = new PopOver();
		TextField textField = new TextField();
		Button create = new Button("Erstelle");
		Button cancel = new Button("Abbrechen");
		GridPane gridPane = new GridPane();

		textField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				e.consume();
				if (!textField.getText().isEmpty()) {
					String name = textField.getText();
					presenter.createNewNotes(name);
				}

				popOver.hide();
			}
		});
		create.setOnAction(e -> {
			e.consume();
			if (!textField.getText().isEmpty()) {
				String name = textField.getText();
				presenter.createNewNotes(name);
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
	private void publicDiceRoll(Event event) {
		event.consume();
		if (rollAmount.getText().isEmpty()) {
			appendChatMessage("Es muss eine Anzahl an würfeln angegeben werden!");
			return;
		}
		if (publicDiceInput.getText().isEmpty()) {
			appendChatMessage("Es muss eine Augenzahl für die Würfel angegeben werden!");
			return;
		}
		int amount = Integer.parseInt(rollAmount.getText());
		int sides = Integer.parseInt(publicDiceInput.getText());

		presenter.rollPublicDice(sides, amount);
	}

	@FXML
	private void sendChatMessage(Event event) {
		String msg = chatInput.getText();
		if (!msg.trim().isEmpty()) {
			event.consume();
			chatInput.clear();
			dispatchChatMessage(msg);
		}
	}

	@FXML
	private void actionInChatInput(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			sendChatMessage(event);
		}
	}

	@FXML
	private void saveCharacter(KeyEvent event) {
		event.consume();
		getPresenter().storeUser();
	}

	@FXML
	private void mouseEventInParticipantList(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
			User user = participantsList.getSelectionModel().getSelectedItem();
			if (user != null) {
				Tab dialog = new DetailPlayerTab(user, true);
				centralTabPane.getTabs().add(dialog);
				centralTabPane.getSelectionModel().select(dialog);
				centralTabPane.requestFocus();
			}
		}
	}

	protected ObservableList<NotesInformation> getDisplayedNotes() {
		return notesListView.getItems();
	}

	protected NotesInformation getSelectedNote() {
		return notesListView.getSelectionModel().getSelectedItem();
	}

	protected void configureCenter(SplitPane splitPane) {
	}

	protected void configureDices(HBox diceRollRoot) {
	}

	protected void configureListViews(HBox listViewRoot) {
	}

	protected void dispatchChatMessage(String message) {
		presenter.sendChatMessage(message);
	}

	@FXML
	protected void keyPressedOnPublicDiceRoll(KeyEvent keyEvent) {
		if (keyEvent.getCode() == KeyCode.ENTER) {
			publicDiceRoll(keyEvent);
		}
	}

	protected Stage getStage() {
		return stage;
	}

	@Override
	public final void setStage(Stage stage) {
		this.stage = stage;
	}

	@Override
	public final void appendChatMessage(String string) {
		FXUtils.runOnApplicationThread(() -> chatListView.getItems().add(new Message(string, MessageTypes.YOUR_MESSAGE, null)));
	}

	@Override
	public final void appendSystemChatMessage(String string) {
		FXUtils.runOnApplicationThread(() -> chatListView.getItems().add(new Message(string, MessageTypes.SYSTEM_MESSAGE, null)));
	}

	@Override
	public final void appendChatMessage(String string, User sender) {
		FXUtils.runOnApplicationThread(() -> chatListView.getItems().add(new Message(string, MessageTypes.OTHER_PLAYERS_MESSAGE, sender)));
	}

	@Override
	public final void yourDiceResult(int sides, int amount, int result) {
		FXUtils.runOnApplicationThread(() -> chatListView.getItems().add(new Message(null, MessageTypes.MY_DICE_ROLL_RESULT, null, sides, result, amount)));
	}

	@Override
	public final void otherDiceResult(int sides, int amount, int result, User sender) {
		FXUtils.runOnApplicationThread(() -> chatListView.getItems().add(new Message(null, MessageTypes.OTHER_DICE_ROLL_RESULT, sender, sides, result, amount)));
	}

	@Override
	public final void setParticipantsList(List<User> participants) {
		// TODO Remove my own user in Presenter
		FXUtils.runOnApplicationThread(() -> {
			Logger.debug("Setting participants: " + participants);
			participantsList.getItems().clear();
			participantsList.getItems().setAll(participants);
		});
	}

	@Override
	public final void clearChatLog() {
		FXUtils.runOnApplicationThread(() -> chatListView.getItems().clear());
	}

	@Override
	public final void setNotes(List<NotesInformation> all) {
		FXUtils.runOnApplicationThread(() -> notesListView.getItems().setAll(all));
	}

	@Override
	public final File requestStorePath() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Character files (*.character)", "*.character");
		fileChooser.getExtensionFilters().add(extFilter);
		return fileChooser.showSaveDialog(stage);
	}

	@Override
	public final void setTitle(String userName) {
		FXUtils.runOnApplicationThread(() -> stage.setTitle(userName));
	}

	@Override
	public final void setPublicDiceResult(int i) {
		if (i < 0) {
			FXUtils.runOnApplicationThread(() -> {
				publicDiceInput.clear();
				publicDiceInput.requestFocus();
			});
		} else {
			FXUtils.runOnApplicationThread(() -> publicDiceResult.setText(Integer.toString(i)));
		}
	}

	@Override
	public final void tryAcceptDownload(String contextText, String name, Consumer<Boolean> callback) {
		FXUtils.runOnApplicationThread(() -> {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Download");
			alert.initOwner(getStage());
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
	public final void focus() {
		FXUtils.runOnApplicationThread(() -> stage.requestFocus());
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
	public final T getPresenter() {
		return presenter;
	}

	@Override
	public void close() {
		presenter = null;
		stage = null;
		root = null;
		scene = null;
	}

	@Override
	public final void setup() {
		URL location = SetupView.class.getClassLoader().getResource("session_view.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(location);
		fxmlLoader.setController(this);

		Parent parent;
		try {
			parent = fxmlLoader.load();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		TextFieldListener.integerOnly(publicDiceInput);
		TextFieldListener.integerOnly(rollAmount);

		FXUtils.runOnApplicationThread(() -> {
			root = parent;
			scene = new Scene(root);
			participantsList.setCellFactory(new ParticipantCellFactory(stage, this::dispatchChatMessage, this::makeCharacterDownload, this::appendSystemChatMessage));
			chatListView.setCellFactory(new ChatCellFactory());
			configure();
		});
	}

	@Override
	public final void display() {
		FXUtils.runOnApplicationThread(() -> {
			stage.setScene(scene);
			root.getStylesheets().clear();
			root.getStylesheets().add(StyleSheetCache.pathToPlayerView());
			stage.setResizable(true);
			stage.setMaximized(true);

			if (!stage.isShowing()) {
				stage.show();
			}
		});
	}
}
