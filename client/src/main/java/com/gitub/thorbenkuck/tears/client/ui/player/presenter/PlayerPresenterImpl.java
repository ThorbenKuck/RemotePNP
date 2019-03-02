package com.gitub.thorbenkuck.tears.client.ui.player.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.*;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
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
import com.gitub.thorbenkuck.tears.client.ui.notes.view.NotesView;
import com.gitub.thorbenkuck.tears.client.ui.player.view.PlayerView;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;
import com.google.common.eventbus.Subscribe;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class PlayerPresenterImpl implements PlayerPresenter {

	private final Repository repository;
	private final CharacterRepository characterRepository;
	private final Client client;
	private final MetaController metaController;
	private PlayerView playerView;
	private DownloadBroker downloadBroker;

	PlayerPresenterImpl(Repository repository, CharacterRepository characterRepository, MetaController metaController) {
		this.repository = repository;
		this.characterRepository = characterRepository;
		this.client = repository.get(Client.class);
		this.metaController = metaController;
	}

	@Subscribe
	private void handle(UploadRequest uploadRequest) {
		playerView.tryAcceptDownload("Die Datei " + uploadRequest.getId() + " steht zum Download bereit veröffentlich werden", uploadRequest.getFileName(), okay -> downloadBroker.receiveDownload(uploadRequest, okay));
	}

	@Subscribe
	private void handle(KillAllSounds killAllSounds) {
		playerView.appendChatMessage("Sounds von Spielleiter beendet");
		killAllSounds();
	}

	@Subscribe
	private void handle(SessionDestroyed destroyed) {
		metaController.show(ServerView.class);
	}

	@Override
	public <T extends View> void inject(T view) {
		playerView = (PlayerView) view;
		downloadBroker = new DownloadBroker(client, playerView::appendChatMessage, repository);
	}

	@Override
	public PlayerView getView() {
		return playerView;
	}

	@Subscribe
	public void handle(PublicRollResponse publicRollResponse) {
		MediaCenter.playDiceRoll();
		if(publicRollResponse.getOrigin().equals(repository.get(User.class))) {
			playerView.setPublicDiceResult(publicRollResponse.getResult());
		}
		playerView.appendChatMessage("[" + publicRollResponse.getOrigin().getUserName() + "] Würfelt d" + publicRollResponse.getSides() + ": " + publicRollResponse.getResult());
	}

	@Subscribe
	public void handle(InSessionUpdate inSessionUpdate) {
		playerView.setParticipantsList(inSessionUpdate.getGameSession().getAll());
	}

	@Subscribe
	public void handle(DisplayImage image) {
		playerView.displayImage(image.getData());
		MediaCenter.playImageDisplayed();
	}

	@Subscribe
	public void handle(ChatMessage chatMessage) {
		playerView.appendChatMessage(chatMessage.getMsg());
		MediaCenter.playChatMessage();
	}

	@Subscribe
	public void handle(PlaySound playSound) {
		if (!MediaCenter.isMuted()) {
			SoundBoard soundBoard = SoundBoard.getInstance();
			Sound sound = soundBoard.find(playSound.getSoundName());
			Logger.debug(sound);
			if (!soundBoard.playSound(sound)) {
				playerView.appendChatMessage("Unknown sound request: " + sound.getName() + " from " + playSound.getUser());
			} else {
				playerView.appendChatMessage("[" + playSound.getUser() + "]: Playing: " + sound.getName());
			}
		}
	}

	@Subscribe
	public void handle(PlayerLeaveEvent playerLeaveEvent) {
		MediaCenter.playDiDo();
		if(!playerLeaveEvent.getUser().equals(repository.get(User.class))) {
			getView().appendChatMessage(playerLeaveEvent.getUser() + " ist gegangen.");
		}
	}

	@Subscribe
	private void handle(PlayerJoinedSession playerJoinedSession) {
		MediaCenter.playDoDi();
		if(!playerJoinedSession.getUser().equals(repository.get(User.class))) {
			getView().appendChatMessage("Willkommen " + playerJoinedSession.getUser() + "!");
		} else {
			getView().appendChatMessage("Du bist der Session beigetreten");
		}
	}

	@Override
	public void store(Character character, String file) {
		characterRepository.storeAndAdd(character, file);
	}

	@Override
	public void updateUser() {
		User user = repository.get(User.class);
		if (characterRepository.updateStoredVersion(user.getCharacter())) {
			playerView.appendChatMessage("In Charakter " + user.getCharacter() + " gespeichert");
			client.send(new UpdateUser(user));
		} else {
			playerView.appendChatMessage("Der Charakter konnte nicht gespeichert werden");
		}
		updateNotes();
		updateBaseValues();
		updateAttributes();
	}

	private void updateNotes() {
		getView().setNotes(repository.get(User.class).getCharacter().getRepository().all());
	}

	@Override
	public void storeUser() {
		User user = repository.get(User.class);
		if (!characterRepository.knownStorage(user.getCharacter())) {
			File file = playerView.requestStorePath();
			characterRepository.mapToFile(user.getCharacter(), file.getAbsolutePath());
		}

		updateUser();
	}

	@Override
	public void editNotes(NotesInformation notesInformation) {
		NotesView notesView = metaController.showSeparate(NotesView.class);
		notesView.getPresenter().setUserPresenter(this);
		notesView.getPresenter().setNotesInformation(notesInformation);
	}

	@Override
	public void createNewNotes(String s) {
		repository.get(User.class).getCharacter().getRepository().add(new NotesInformation(s));
		updateUser();
	}

	@Override
	public void privateRoll(String text) {
		try {
			int numberSides = Integer.parseInt(text);
			int result = ThreadLocalRandom.current().nextInt(numberSides) + 1;
			playerView.appendChatMessage("Privates Würfelergebniss: " + result);
		} catch (NumberFormatException e) {
			playerView.appendChatMessage("Die Würfel Seite muss eine Zahl Sein!");
		}
	}

	@Override
	public void dispatchChatMessage(String message) {
		if (message.equals("/clear")) {
			playerView.clearChatLog();
		} else if (message.startsWith("/roll")) {
			String sides = message.substring(5);
			privateRoll(sides);
		} else if (message.startsWith("/map")) {
			String map = message.substring(5);
			Sound sound = SoundBoard.handleMapping(map);
			if (sound == null) {
				playerView.appendChatMessage("Command usage: /map <original-name>:/path/to/your/file.mp3");
			} else {
				playerView.appendChatMessage("Temporarily mapped " + sound.getName() + " to " + sound.getPath());
			}
		} else if(!message.isEmpty()) {
			client.send(new ChatMessage(message, repository.get(GameSession.class)));
		}
	}

	@Override
	public void publicRoll(String text) {
		try {
			int numberSides = Integer.parseInt(text);
			client.send(new PublicRollRequest(repository.get(User.class), numberSides, repository.get(GameSession.class)));
		} catch (NumberFormatException e) {
			playerView.setPublicDiceResult(-1);
			playerView.appendChatMessage("Die Würfel Seite muss eine Zahl Sein!");
		}
	}

	@Override
	public void updateBaseValues() {
		playerView.setBaseValues(repository.get(User.class).getCharacter().getBaseValues());
	}

	@Override
	public void updateAttributes() {
		playerView.setAttributes(repository.get(User.class).getCharacter().getAttributeList());
	}

	@Override
	public void removeAttribute(Attribute item) {
		User user = repository.get(User.class);
		List<Attribute> attributeList = user.getCharacter().getAttributeList();

		List<Attribute> collect = attributeList.stream()
				.filter(attribute -> attribute.getName().equals(item.getName()))
				.collect(Collectors.toList());

		collect.forEach(attributeList::remove);

		updateUser();
	}

	@Override
	public void removeBaseValue(BaseValue item) {
		User user = repository.get(User.class);
		List<BaseValue> baseValues = user.getCharacter().getBaseValues();

		List<BaseValue> collect = baseValues.stream()
				.filter(attribute -> attribute.getName().equals(item.getName()))
				.collect(Collectors.toList());

		collect.forEach(baseValues::remove);

		updateUser();
	}

	@Override
	public void backToServerView() {
		client.send(new LeaveGameSessionRequest(repository.get(GameSession.class)));
	}

	@Override
	public void killAllSounds() {
		MediaCenter.killAll();
	}

	@Override
	public void cleanUp() {
		playerView = null;
	}

	@Override
	public void setup() {

	}

	@Override
	public void afterDisplay() {
		playerView.setTitle(repository.get(User.class).getUserName()+ "@" + client.getAddress());
		playerView.setParticipantsList(repository.get(GameSession.class).getAll());
		updateBaseValues();
		updateAttributes();
		updateNotes();
		client.send(new UpdateUser(repository.get(User.class)));
	}
}
