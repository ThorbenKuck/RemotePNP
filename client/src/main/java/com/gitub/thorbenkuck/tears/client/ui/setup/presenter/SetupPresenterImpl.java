package com.gitub.thorbenkuck.tears.client.ui.setup.presenter;

import com.github.thorbenkuck.tears.shared.Settings;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.exceptions.ConnectionEstablishmentFailedException;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.JoinServerRequest;
import com.github.thorbenkuck.tears.shared.messages.JoinServerResponse;
import com.github.thorbenkuck.tears.shared.network.Connection;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.network.Client;
import com.gitub.thorbenkuck.tears.client.ui.FXUtils;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.character.view.CharacterView;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.function.Consumer;

class SetupPresenterImpl implements SetupPresenter {

	private final MetaController metaController;
	private final CharacterRepository characterRepository;
	private final Repository repository;
	private final EventBus eventBus;
	private final Settings settings;
	private final Observer observer;
	private SetupView setupView;

	SetupPresenterImpl(MetaController metaController, CharacterRepository characterRepository, Repository repository, EventBus eventBus, Settings settings) {
		this.metaController = metaController;
		this.characterRepository = characterRepository;
		this.repository = repository;
		this.eventBus = eventBus;
		this.observer = (o, arg) -> setupView.setCharacters(characterRepository.getAll());
		this.settings = settings;
	}

	@Override
	public <T extends View> void inject(T view) {
		this.setupView = (SetupView) view;
	}

	@Override
	public SetupView getView() {
		return setupView;
	}

	@Override
	public void createNewCharacter() {
		metaController.showSeparate(CharacterView.class);
	}

	@Override
	public void connect(String server, String username, Character selectedCharacter) {
		setupView.setFeedback("Versuche eine Verbindung auf zu bauen...");
		int port = settings.getInt("server.port", 8880);
		settings.set("username.last", username);
		settings.set("character.last", selectedCharacter.getCharacterName());

		Client client = Client.create(server, port, repository);

		try {
			client.launch();
		} catch (ConnectionEstablishmentFailedException e) {
			setupView.setErrorFeedback("Der Server " + server + ":" + port + " ist nicht erreichbar!");
			return;
		}

		Runtime.getRuntime().addShutdownHook(new Thread(client::close));

		client.handleSpecific(JoinServerResponse.class, message -> {
			if (!message.getCore().isSuccess()) {
				setupView.setErrorFeedback("Der Server lehnt dich ab. Grund: " + message.getCore().getMessage());
			} else {
				setupView.setFeedback("Du hast dich verbunden. Öffne die Server-Ansicht..");
				Logger.debug("Storing information about your user object: " + message.getCore().getUser());
				repository.add(message.getCore().getUser());
				metaController.show(ServerView.class);
				client.registerTo(eventBus);
				client.disconnectedPipeline().add(connection -> {
					FXUtils.runOnApplicationThread(() -> Notifications.create()
								.title("Disconnected!")
								.text("Du hast die Verbindung zu " + server + " verloren!")
								.showError());
//					try {
//						client.launch();
//					} catch (ConnectionEstablishmentFailedException e) {
//						Platform.exit();
//					}
				});

				FXUtils.runOnApplicationThread(() -> Notifications.create()
						.title("Connected")
						.text("Verbunden als " + username + " zu " + server)
						.showConfirm());
			}
		});

		client.disconnectedPipeline().add((Consumer<Connection>) foo -> metaController.show(SetupView.class));

		repository.add(Client.class, client);

		setupView.setFeedback("Die Verbindung wurde aufgebaut! Trete dem Server bei..");

		User user = new User(username, selectedCharacter);
		Logger.debug("Requesting server join");
		client.send(new JoinServerRequest(user));


	}

	private void showSystemTray(String server, String username) {
//		SystemTray systemTray = SystemTray.get();
//		if (systemTray == null) {
//			throw new RuntimeException("Unable to load SystemTray!");
//		}
//
//		systemTray.setStatus("Not Running");
//
//		systemTray.getMenu().add(new MenuItem("Quit", e -> {
//			systemTray.shutdown();
//			//System.exit(0);  not necessary if all non-daemon threads have stopped.
//		})).setShortcut('q'); // case does not matter
	}

	@Override
	public void loadCharacters(List<File> files) {
		files.forEach(file -> {
			String path = file.getAbsolutePath();
			if (!characterRepository.load(path)) {
				setupView.setFeedback("Die Datei " + path + " konnte nicht geladen werden!");
			}
		});
	}

	@Override
	public void cleanUp() {
		// In here, we want do destroy
		// every reference we have, so
		// that the gc my collect us
		// and also them
		this.setupView = null;
		characterRepository.deleteObserver(observer);
	}

	@Override
	public void setup() {
		// No setup needed
		characterRepository.addObserver(observer);
	}

	@Override
	public void afterDisplay() {
		getView().setFeedback("Lade Charaktere ..");
		settings.load();
		List<String> characterPaths = settings.getCharacterPaths();
		List<String> valid = new ArrayList<>();
		// Even though IntelliJ says, this can be
		// "simplyfied", we do not want to do that!
		// This would change the behaviour! The last
		// entry is the most relevant
		characterPaths.stream().forEachOrdered(path -> {
			getView().setFeedback("Loading " + path);
			if (characterRepository.load(path)) {
				valid.add(path);
			} else {
				Logger.warn("Not valid path found: " + path);
			}
		});

		String address = settings.get("server.address", "");
		Logger.debug("Using ServerAddress " + address);
		getView().setServer(address);
		getView().setUsername(settings.get("username.last", ""));
		getView().trySelectCharacter(settings.get("character.last", ""));

		if (!characterPaths.equals(valid)) {
			getView().setFeedback("Storing valid paths..");
			settings.setCharacterPaths(valid);
		}

		getView().setFeedback("Charaktere geladen!");
	}
}
