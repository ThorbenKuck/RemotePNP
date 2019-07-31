package com.gitub.thorbenkuck.tears.client.ui.session.player.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.*;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.media.DownloadBroker;
import com.gitub.thorbenkuck.tears.client.media.MediaCenter;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.Sound;
import com.gitub.thorbenkuck.tears.client.ui.SoundBoard;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;
import com.gitub.thorbenkuck.tears.client.ui.session.AbstractSessionPresenter;
import com.gitub.thorbenkuck.tears.client.ui.session.player.view.PlayerView;
import com.google.common.eventbus.Subscribe;

import java.util.List;
import java.util.stream.Collectors;

class PlayerPresenterImpl extends AbstractSessionPresenter<PlayerView> implements PlayerPresenter {

	private DownloadBroker downloadBroker;

	PlayerPresenterImpl(Repository repository, CharacterRepository characterRepository, MetaController metaController) {
		super(characterRepository, metaController, repository);
	}

	@Subscribe
	private void handle(UploadRequest uploadRequest) {
		getView().tryAcceptDownload("Die Datei " + uploadRequest.getId() + " steht zum Download bereit veröffentlich werden", uploadRequest.getFileName(), okay -> downloadBroker.receiveDownload(uploadRequest, okay));
	}

	@Subscribe
	private void handle(KillAllSounds killAllSounds) {
		getView().appendChatMessage("Sounds von Spielleiter beendet");
		killAllSounds();
	}

	@Subscribe
	private void handle(SessionDestroyed destroyed) {
		metaController.show(ServerView.class);
	}

	@Subscribe
	private void handle(PlayerJoinedSession playerJoinedSession) {
		MediaCenter.playDoDi();
		if (!playerJoinedSession.getUser().equals(getUser())) {
			getView().appendChatMessage("Willkommen " + playerJoinedSession.getUser() + "!");
		} else {
			getView().appendChatMessage("Du bist der Session beigetreten");
		}
	}

	@Subscribe
	public void handle(PublicRollResponse publicRollResponse) {
		MediaCenter.playDiceRoll();
		if (publicRollResponse.getOrigin().equals(getUser())) {
			getView().setPublicDiceResult(publicRollResponse.getResult());
		}
		getView().appendChatMessage("[" + publicRollResponse.getOrigin().getUserName() + "] Würfelt " + publicRollResponse.getAmount() + "d" + publicRollResponse.getSides() + ": " + publicRollResponse.getResult());
	}

	@Subscribe
	public void handle(InSessionUpdate inSessionUpdate) {
		getView().setParticipantsList(inSessionUpdate.getGameSession().getAll());
	}

	@Subscribe
	public void handle(DisplayImage image) {
		getView().displayImage(image.getData());
		MediaCenter.playImageDisplayed();
	}

	@Subscribe
	public void handle(ChatMessageResponse chatMessage) {
		if(chatMessage.getOrigin().equals(getUser())) {
			getView().appendChatMessage(chatMessage.getChatMessage().getMsg());
		} else {
			getView().appendChatMessage(chatMessage.getChatMessage().getMsg(), chatMessage.getOrigin());
		}
		MediaCenter.playChatMessage();
	}

	@Subscribe
	public void handle(PlaySound playSound) {
		if (!MediaCenter.isMuted()) {
			SoundBoard soundBoard = SoundBoard.getInstance();
			Sound sound = soundBoard.find(playSound.getSoundName());
			Logger.debug(sound);
			if (!soundBoard.playSound(sound)) {
				getView().appendChatMessage("Unknown sound request: " + sound.getName() + " from " + playSound.getUser());
			} else {
				getView().appendChatMessage("[" + playSound.getUser() + "]: Playing: " + sound.getName());
			}
		}
	}

	@Subscribe
	public void handle(PlayerLeaveEvent playerLeaveEvent) {
		MediaCenter.playDiDo();
		if (!playerLeaveEvent.getUser().equals(getUser())) {
			getView().appendChatMessage(playerLeaveEvent.getUser() + " ist gegangen.");
		}
	}

	@Override
	protected void userChanged() {

	}

	@Override
	public <T extends View> void inject(T view) {
		setView((PlayerView) view);
		downloadBroker = new DownloadBroker(client, getView()::appendChatMessage);
	}

	@Override
	public void updateUser() {
		User user = getUser();
		if (characterRepository.updateStoredVersion(user.getCharacter())) {
			getView().appendChatMessage("In Charakter " + user.getCharacter() + " gespeichert");
			client.send(new UpdateUser(user));
		} else {
			getView().appendChatMessage("Der Charakter konnte nicht gespeichert werden");
		}
		updateNotes();
		updateBaseValues();
		updateAttributes();
	}

	@Override
	public void updateBaseValues() {
		getView().setBaseValues(getUser().getCharacter().getBaseValues());
	}

	@Override
	public void updateAttributes() {
		getView().setAttributes(getUser().getCharacter().getAttributeList());
	}

	@Override
	public void removeAttribute(Attribute item) {
		User user = getUser();
		List<Attribute> attributeList = user.getCharacter().getAttributeList();

		List<Attribute> collect = attributeList.stream()
				.filter(attribute -> attribute.getName().equals(item.getName()))
				.collect(Collectors.toList());

		collect.forEach(attributeList::remove);

		updateUser();
	}

	@Override
	public void removeBaseValue(BaseValue item) {
		User user = getUser();
		List<BaseValue> baseValues = user.getCharacter().getBaseValues();

		List<BaseValue> collect = baseValues.stream()
				.filter(attribute -> attribute.getName().equals(item.getName()))
				.collect(Collectors.toList());

		collect.forEach(baseValues::remove);

		updateUser();
	}

	@Override
	public void backToServerView() {
		client.send(new LeaveGameSessionRequest(getGameSession()));
	}

	@Override
	public void afterDisplay() {
		getView().setTitle(getUser().getUserName() + "@" + client.getAddress());
		getView().setParticipantsList(getGameSession().getAll());
		updateBaseValues();
		updateAttributes();
		updateNotes();
		client.send(new UpdateUser(getUser()));
	}
}
