package com.gitub.thorbenkuck.tears.client.ui.gm.presenter;

import com.github.thorbenkuck.tears.shared.SoundDownloadAndMapRequest;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.*;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.media.DownloadBroker;
import com.gitub.thorbenkuck.tears.client.media.MediaCenter;
import com.gitub.thorbenkuck.tears.client.network.Client;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.Sound;
import com.gitub.thorbenkuck.tears.client.ui.SoundBoard;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.gm.view.GMView;
import com.gitub.thorbenkuck.tears.client.ui.notes.view.NotesView;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;
import com.google.common.eventbus.Subscribe;

import java.io.File;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class GMPresenterImpl implements GMPresenter {

	private final Repository repository;
	private final Random random = ThreadLocalRandom.current();
	private final Client client;
	private final User user;
	private final GameSession gameSession;
	private final CharacterRepository characterRepository;
	private final MetaController metaController;
	private DownloadBroker downloadBroker;
	private GMView gmView;

	GMPresenterImpl(Repository repository, CharacterRepository characterRepository, MetaController metaController) {
		this.repository = repository;
		this.client = repository.get(Client.class);
		this.characterRepository = characterRepository;
		this.metaController = metaController;
		User user = repository.get(User.class);
		GameSession active = repository.get(GameSession.class);

		if (user == null) {
			throw new NullPointerException("No user found in repository");
		}

		if (active == null) {
			throw new NullPointerException("No game session found in repository");
		}

		this.user = user;
		this.gameSession = active;
	}

	@Override
	public void backToServerView() {
		client.send(new LeaveGameSessionRequest(repository.get(GameSession.class)));
	}

	@Override
	public <T extends View> void inject(T view) {
		gmView = (GMView) view;
		downloadBroker = new DownloadBroker(client, gmView::appendChatMessage, repository);
	}

	@Override
	public GMView getView() {
		return gmView;
	}

	@Subscribe
	private void handle(SessionDestroyed destroyed) {
		metaController.show(ServerView.class);
	}

	@Subscribe
	private void handle(KillAllSounds killAllSounds) {
		MediaCenter.killAll();
	}

	@Subscribe
	public void handle(PublicRollResponse publicRollResponse) {
		MediaCenter.playDiceRoll();
		if(publicRollResponse.getOrigin().equals(user)) {
			gmView.setPublicDiceResult(publicRollResponse.getResult());
		}
		gmView.appendChatMessage("[" + publicRollResponse.getOrigin().getUserName() + "] Würfelt d" + publicRollResponse.getSides() + ": " + publicRollResponse.getResult());
	}

	@Subscribe
	public void handle(InSessionUpdate update) {
		gmView.setParticipantsList(update.getGameSession().getParticipants());
	}

	@Subscribe
	public void handle(ChatMessage chatMessage) {
		gmView.appendChatMessage(chatMessage.getMsg());
		MediaCenter.playChatMessage();
	}

	@Subscribe
	public void handle(PlaySound playSound) {
		if (!MediaCenter.isMuted()) {
			Sound sound = SoundBoard.getInstance().find(playSound.getSoundName());
			if (!SoundBoard.getInstance().playSound(sound)) {
				gmView.appendChatMessage("Unknown sound request: " + sound.getName() + " from " + playSound.getUser());
			} else {
				gmView.appendChatMessage("[" + playSound.getUser() + "]: Playing: " + sound.getName());
			}
		}
	}

	@Subscribe
	private void handle(PlayerJoinedSession playerJoinedSession) {
		MediaCenter.playDoDi();
		if(!playerJoinedSession.getUser().equals(user)) {
			gmView.appendChatMessage("Willkommen " + playerJoinedSession.getUser() + "!");
		} else {
			gmView.appendChatMessage("Du bist der Session beigetreten");
		}
	}

	@Subscribe
	public void handle(PlayerLeaveEvent playerLeaveEvent) {
		MediaCenter.playDiDo();
		if(!playerLeaveEvent.getUser().equals(user)) {
			gmView.appendChatMessage(playerLeaveEvent.getUser() + " ist gegangen.");
		}
	}

	@Subscribe
	public void handle(SoundDownloadAndMapRequest request) {
		if(SoundBoard.getInstance().integrate(request)) {
			gmView.updateSoundBoard();
			gmView.appendChatMessage("Neuer Sound empfangen: Name=" + request.getMapName() + ", Soundname=" + request.getFilename());
		}
	}

	@Subscribe
	public void handle(UploadAccepted uploadAccepted) {
		getView().appendChatMessage("[" + uploadAccepted.getUser() + "] startet " + uploadAccepted.getId());
	}

	@Subscribe
	public void handle(UploadRejected uploadRejected) {
		getView().appendChatMessage("[ABGELEHNT] {" + uploadRejected.getUser() + "} : " + uploadRejected.getId());
	}

	@Subscribe
	public void handle(UploadFinished uploadFinished) {
		getView().appendChatMessage("[" + uploadFinished.getUser() + "] fertig!");
	}

	@Override
	public void publicRoll(String text) {
		try {
			int numberSides = Integer.parseInt(text);
			client.send(new PublicRollRequest(user, numberSides, gameSession));
		} catch (NumberFormatException e) {
			gmView.setPrivateDiceResult(-1);
			gmView.appendChatMessage("Die Würfel Seite muss eine Zahl Sein!");
		}
	}

	@Override
	public void privateRoll(String text) {
		try {
			int numberSides = Integer.parseInt(text);
			int result = random.nextInt(numberSides) + 1;
			gmView.setPrivateDiceResult(result);
			gmView.appendChatMessage("Privates Würfelergebniss: " + result);
		} catch (NumberFormatException e) {
			gmView.setPrivateDiceResult(-1);
			gmView.appendChatMessage("Die Würfel Seite muss eine Zahl Sein!");
		}
	}

	@Override
	public void dispatchChatMessage(String message) {
		if (message.equals("/clear")) {
			Logger.debug("Clearing local chat log");
			gmView.clearChatLog();
		} else if (message.startsWith("/roll")) {
			String sides = message.substring(5);
			Logger.debug("Rolling local dice with " + sides + " sides");
			privateRoll(sides);
		}  else if(message.startsWith("/map")) {
			String map = message.substring(5);
			Sound sound = SoundBoard.handleMapping(map);
			if(sound == null) {
				gmView.appendChatMessage("Command usage: /map <original-name>:/path/to/your/file.mp3");
			} else {
				gmView.appendChatMessage("Temporarily mapped " + sound.getName() + " to " + sound.getPath());
				gmView.updateSoundBoard();
			}
		} else if(!message.isEmpty()) {
			Logger.debug("Sending message to Server: " + message);
			client.send(new ChatMessage(message, gameSession));
		}
	}

	@Override
	public void storeUser() {
		if (!characterRepository.knownStorage(user.getCharacter())) {
			File file = gmView.requestStorePath();
			characterRepository.mapToFile(user.getCharacter(), file.getAbsolutePath());
		}

		if (characterRepository.updateStoredVersion(user.getCharacter())) {
			gmView.appendChatMessage("In Charakter " + user.getCharacter() + " gespeichert");
			client.send(new UpdateUser(user));
		} else {
			gmView.appendChatMessage("Der Charakter konnte nicht gespeichert werden");
		}
	}

	@Override
	public void editNotes(NotesInformation notesInformation) {
		NotesView notesView = metaController.showSeparate(NotesView.class);
		notesView.getPresenter().setUserPresenter(this);
		notesView.getPresenter().setNotesInformation(notesInformation);
	}

	@Override
	public void createNewNotes(String s) {
		user.getCharacter().getRepository().add(new NotesInformation(s));
		updateUser();
	}

	private void updateNotes() {
		getView().setNotes(user.getCharacter().getRepository().all());
	}

	private void updateUser() {
		User user = repository.get(User.class);
		if (characterRepository.updateStoredVersion(user.getCharacter())) {
			gmView.appendChatMessage("In Charakter " + user.getCharacter() + " gespeichert");
			client.send(new UpdateUser(user));
			updateNotes();
		} else {
			gmView.appendChatMessage("Der Charakter konnte nicht gespeichert werden");
		}
	}

	@Override
	public void sendToAll(byte[] res) {
		new Thread(() -> client.send(new ImageToDisplayMessage(res, gameSession))).start();
	}

	@Override
	public void sendToSpecific(User user, byte[] res) {
		String userName = user.getUserName();
		client.send(new ImageToDisplayMessage(res, Collections.singletonList(userName), gameSession));
	}

	@Override
	public void playOnAll(Sound item) {
		client.send(new PlaySound(item.getName()));
	}

	@Override
	public void store(Character character, String s) {
		characterRepository.storeAndAdd(character, s);
	}

	@Override
	public void requestSendingOf(File file, String name) {
		if(!file.exists()) {
			Logger.warn("Could not locate the requested file");
			return;
		}

		downloadBroker.startDownload(file, name);
		SoundBoard.getInstance().customMapping(file, name);
		gmView.updateSoundBoard();
	}

	@Override
	public void requestSendingOf(File file, User target, String name) {
		if(!file.exists()) {
			Logger.warn("Could not locate the requested file");
			return;
		}

		downloadBroker.startDownload(file, target, name);
		SoundBoard.getInstance().customMapping(file, name);
		gmView.updateSoundBoard();
	}

	@Override
	public void killAllSounds() {
		client.send(new KillAllSounds());
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public void cleanUp() {
		gmView = null;
	}

	@Override
	public void setup() {

	}

	@Override
	public void afterDisplay() {
		gmView.setTitle(user.getUserName() + "@" + client.getAddress());
		updateNotes();
		client.send(new UpdateUser(repository.get(User.class)));
	}
}
