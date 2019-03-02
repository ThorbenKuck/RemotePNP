package com.github.thorbenkuck.tears.launcher;

import com.github.thorbenkuck.tears.shared.Settings;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class LauncherMain extends Application {

	private final Settings settings = new Settings();
	private Semaphore exitAccess = new Semaphore(0);
	private TextArea textArea;
	private UpdateNode updateNode;
	private Stage stage;
	private Label label;

	public static void main(String[] args) {
		launch(args);
	}

	private void tryStart() {
		Thread thread = new Thread(() -> {
			try {
				updateNode.restart();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			try {
				exitAccess.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Logger.debug("Exiting JavaFX");
			Platform.exit();
			System.out.println("Bye!");
			System.exit(0);
			Logger.info("System exited. Bye");
		});
		thread.setName("Process head");
		thread.start();
		Platform.runLater(() -> stage.hide());
	}

	private void labelInfo(String message) {
		label.setTextFill(Color.DARKBLUE);
	}

	private void labelError(String message) {
		label.setTextFill(Color.DARKRED);
	}

	public void appendMessage(String message) {
		appendRawMessage(message + "\n");
	}

	public void appendRawMessage(String message) {
		textArea.appendText(message);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			Logger.catching(e);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		label = new Label();
		textArea = new TextArea("Willkommen! Lass mich nach der neusten Version für dich suchen..\n");

		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UNDECORATED);

		GridPane gridPane = new GridPane();

		label.setPrefWidth(600);
		label.setTextAlignment(TextAlignment.CENTER);
		textArea.setEditable(false);

		gridPane.add(label, 0, 0);
		gridPane.add(textArea, 0, 1);

		settings.load();
		int version = settings.getInt("remote.client.version");

		int port = settings.getInt("launcher.update.hub.port", 8881);
		String potentialServer = settings.get("launcher.update.hub.address", "NULL");

		labelInfo("Nicht verbunden");
		primaryStage.setScene(new Scene(gridPane));
		primaryStage.show();

		if (potentialServer.equals("NULL") || potentialServer.equals("")) {
			TextInputDialog textInputDialog = new TextInputDialog("localhost");
			textInputDialog.setTitle("Server Addresse");
			textInputDialog.setHeaderText("Es liegt uns keine Server-Addresse vor!");
			textInputDialog.setContentText("Bitt gebe eine Addresse ein:");
			textInputDialog.initOwner(stage);

			Optional<String> optional = textInputDialog.showAndWait();
			if (optional.isPresent()) {
				potentialServer = optional.get();
			} else {
				appendMessage("Kein Server angegeben!");
				appendMessage("Beende..");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.exit();
			}
		}

		final String server = potentialServer;

		new Thread(() -> {
			try {
				Logger.debug("Checking for updates");
				appendRawMessage("Verbindungsaufbau zu " + server + ":" + port + " ... ");
				updateNode = new UpdateNode(server, port);
				appendMessage("[OK]");
				Logger.debug("Updating stored credentials");
				settings.set("launcher.update.hub.port", port);
				settings.set("launcher.update.hub.address", server);
				settings.set("server.address", server);
				settings.set("server.port", 8880);
				settings.store();
				updateNode.listen();
				appendRawMessage("Checke die Version ... ");
				if (updateNode.newVersionAvailable(version)) {
					appendMessage("[OK]");
					Logger.info("New version available!");
					appendMessage("Eine neue Version ist verfügbar!");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Logger.catching(e);
					}
					appendMessage("Version: " + updateNode.getVersion());
					appendRawMessage("Lade herunter ... ");
					updateNode.download();
					appendMessage("[OK]");
					appendMessage("Download Abgeschlossen!");
					settings.set("remote.client.version", updateNode.getVersion());
					settings.store();
					Platform.runLater(() -> {
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setTitle("Neue Version Heruntergeladen");
						alert.setHeaderText("Es wurde eine neue Version herunter geladen");
						alert.setContentText("Die Version " + updateNode.getVersion() + " wurde soeben herunter geladen.");
						TextArea textArea = new TextArea(parse(updateNode.getDescription()));
						textArea.setWrapText(true);
						textArea.setEditable(false);
						alert.setResizable(true);
						alert.getDialogPane().setExpandableContent(textArea);
						alert.showAndWait();
						exitAccess.release();
					});
				} else {
					appendMessage("[OK]");
					appendMessage("Du hast bereits die neuste Version!");
					exitAccess.release();
				}
				tryStart();
			} catch (IOException e) {
				Logger.catching(e);
				appendMessage("[ERROR]");
				appendMessage("Keine Verbindung zum Update-Server!");
			}
		}).start();
	}

	private String parse(String s) {
		return s.replaceAll("#br#", System.lineSeparator());
	}
}
