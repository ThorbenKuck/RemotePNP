package com.gitub.thorbenkuck.tears.client.ui.session;

import com.github.thorbenkuck.tears.shared.datatypes.*;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.messages.ChatMessage;
import com.github.thorbenkuck.tears.shared.messages.PublicRollRequest;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.media.MediaCenter;
import com.gitub.thorbenkuck.tears.client.network.Client;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.SessionPresenter;
import com.gitub.thorbenkuck.tears.client.ui.SessionView;
import com.gitub.thorbenkuck.tears.client.ui.notes.view.NotesView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractSessionPresenter<V extends SessionView> implements SessionPresenter {

	private V view;
	private User user;
	private GameSession gameSession;

	protected final CharacterRepository characterRepository;
	protected final Client client;
	protected final MetaController metaController;
	protected final Repository repository;

	protected AbstractSessionPresenter(CharacterRepository characterRepository, MetaController metaController, Repository repository) {
		this.characterRepository = characterRepository;
		this.client = repository.get(Client.class);
		this.metaController = metaController;
		this.repository = repository;

		setUser(repository.get(User.class));
		setGameSession(repository.get(GameSession.class));
	}

	private void updateUser() {
		if (characterRepository.updateStoredVersion(user.getCharacter())) {
			view.appendSystemChatMessage("In Charakter " + user.getCharacter() + " gespeichert");
			userChanged();
		} else {
			view.appendSystemChatMessage("Der Charakter konnte nicht gespeichert werden");
		}
	}

	// TODO Update Notes/Attributes/Values/Send to Server/ ...
	protected abstract void userChanged();

	protected void updateNotes() {
		getView().setNotes(user.getCharacter().getRepository().all());
	}

	@Override
	public void privateRoll(String text) {
		try {
			int numberSides = Integer.parseInt(text);
			int result = ThreadLocalRandom.current().nextInt(numberSides) + 1;
			view.appendChatMessage("Privates Würfelergebniss: " + result);
		} catch (NumberFormatException e) {
			view.appendSystemChatMessage("Die Würfel Seite muss eine Zahl Sein!");
		}
	}

	@Override
	public final V getView() {
		return view;
	}

	protected final void setView(V view) {
		this.view = view;
	}

	@Override
	public final void cleanUp() {
		view = null;
	}

	@Override
	public void setup() {
	}

	@Override
	public void afterDisplay() {
	}

	@Override
	public void destroy() {
	}

	@Override
	public final void sendChatMessage(String message) {
		if (message.equals("/clear")) {
			view.clearChatLog();
		} else if (message.startsWith("/roll")) {
			String sides = message.substring(5);
			privateRoll(sides);
		} else if (!message.isEmpty()) {
			client.send(new ChatMessage(message, gameSession));
		}
	}

	@Override
	public final User getUser() {
		return user;
	}

	@Override
	public final void setUser(User user) {
		this.user = user;
	}

	@Override
	public final GameSession getGameSession() {
		return gameSession;
	}

	@Override
	public void setGameSession(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	@Override
	public final void rollPublicDice(int sides, int amount) {
		try {
			client.send(new PublicRollRequest(user, sides, amount, gameSession));
		} catch (NumberFormatException e) {
			view.setPublicDiceResult(-1);
			view.appendSystemChatMessage("Die Würfel Seite und Anzahl muss eine Zahl Sein!");
		}
	}

	@Override
	public final void store(Character character, String file) {
		characterRepository.storeAndAdd(character, file);
	}

	@Override
	public final void storeUser() {
		if (!characterRepository.knownStorage(user.getCharacter())) {
			File file = view.requestStorePath();
			characterRepository.mapToFile(user.getCharacter(), file.getAbsolutePath());
		}

		updateUser();
	}

	@Override
	public final void editNotes(NotesInformation notesInformation) {
		NotesView notesView = metaController.showSeparate(NotesView.class);
		notesView.getPresenter().setUserPresenter(this);
		notesView.getPresenter().setNotesInformation(notesInformation);
	}

	@Override
	public final void createNewNotes(String name) {
		user.getCharacter().getRepository().add(new NotesInformation(name));
		updateUser();
	}

	@Override
	public final void lowerVolume() {
		if (MediaCenter.lowerVolume()) {
			view.appendSystemChatMessage("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
		}
	}

	@Override
	public final void higherVolume() {
		if (MediaCenter.higherVolume()) {
			view.appendSystemChatMessage("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
		}
	}

	@Override
	public final void killAllSounds() {
		MediaCenter.killAll();
	}

	@Override
	public final void mute() {
		MediaCenter.changeMute();
		if (MediaCenter.isMuted()) {
			view.appendSystemChatMessage("Sound deactivated");
		} else {
			view.appendSystemChatMessage("Sound activated");
		}
	}

	@Override
	public final void setParticipants(List<User> users) {
		view.setParticipantsList(users);
	}
}
