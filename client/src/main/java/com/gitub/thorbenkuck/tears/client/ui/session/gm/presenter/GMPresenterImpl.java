package com.gitub.thorbenkuck.tears.client.ui.session.gm.presenter;

import com.github.thorbenkuck.tears.shared.SoundDownloadAndMapRequest;
import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
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
import com.gitub.thorbenkuck.tears.client.ui.session.AbstractSessionPresenter;
import com.gitub.thorbenkuck.tears.client.ui.session.gm.view.GMView;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;
import com.google.common.eventbus.Subscribe;

import java.io.File;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class GMPresenterImpl extends AbstractSessionPresenter<GMView> implements GMPresenter {

	private final Random random = ThreadLocalRandom.current();
	private DownloadBroker downloadBroker;
	private GMView gmView;

	GMPresenterImpl(Repository repository, CharacterRepository characterRepository, MetaController metaController) {
		super(characterRepository, metaController, repository);
	}

	@Override
	public void backToServerView() {
		client.send(new LeaveGameSessionRequest(repository.get(GameSession.class)));
	}

	@Override
	public <T extends View> void inject(T view) {
		gmView = (GMView) view;
		downloadBroker = new DownloadBroker(client, gmView::appendChatMessage);
		setView(gmView);
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
		if(publicRollResponse.getOrigin().equals(getUser())) {
			gmView.setPublicDiceResult(publicRollResponse.getResult());
		}
		gmView.appendChatMessage("[" + publicRollResponse.getOrigin().getUserName() + "] Würfelt " + publicRollResponse.getAmount() + "d" + publicRollResponse.getSides() + ": " + publicRollResponse.getResult());
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
		if(!playerJoinedSession.getUser().equals(getUser())) {
			gmView.appendChatMessage("Willkommen " + playerJoinedSession.getUser() + "!");
		} else {
			gmView.appendChatMessage("Du bist der Session beigetreten");
		}
	}

	@Subscribe
	public void handle(PlayerLeaveEvent playerLeaveEvent) {
		MediaCenter.playDiDo();
		if(!playerLeaveEvent.getUser().equals(getUser())) {
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
	public void publicRoll(String text, String amount) {
		try {
			int numberSides = Integer.parseInt(text);
			int amountRolls = Integer.parseInt(amount);
			client.send(new PublicRollRequest(getUser(), numberSides, amountRolls, getGameSession()));
		} catch (NumberFormatException e) {
			gmView.setPublicDiceResult(-1);
			gmView.appendChatMessage("Die Würfel Seite und Anzahl muss eine Zahl Sein!");
		}
	}

	@Override
	public void privateRoll(String text, String amount) {
		try {
			int numberSides = Integer.parseInt(text);
			int rollAmount = Integer.parseInt(amount);
			int result = 0;

			for(int i = 0 ; i < rollAmount ; i++) {
				result += random.nextInt(numberSides) + 1;
			}

			gmView.setPrivateDiceResult(result);
			gmView.appendChatMessage("Privates Würfelergebniss (" + amount + "d" + text + "): " + result);
		} catch (NumberFormatException e) {
			gmView.setPrivateDiceResult(-1);
			gmView.appendChatMessage("Die Würfel Seite muss eine Zahl Sein!");
		}
	}

	@Override
	protected void userChanged() {
		client.send(new UpdateUser(getUser()));
		updateNotes();
	}

	@Override
	public void sendToAll(byte[] res) {
		new Thread(() -> client.send(new ImageToDisplayMessage(res, getGameSession()))).start();
	}

	@Override
	public void sendToSpecific(User user, byte[] res) {
		String userName = user.getUserName();
		client.send(new ImageToDisplayMessage(res, Collections.singletonList(userName), getGameSession()));
	}

	@Override
	public void playOnAll(Sound item) {
		client.send(new PlaySound(item.getName()));
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
	public void setup() {

	}

	@Override
	public void afterDisplay() {
		gmView.setTitle(getUser().getUserName() + "@" + client.getAddress());
		updateNotes();
		client.send(new UpdateUser(repository.get(User.class)));
	}
}
