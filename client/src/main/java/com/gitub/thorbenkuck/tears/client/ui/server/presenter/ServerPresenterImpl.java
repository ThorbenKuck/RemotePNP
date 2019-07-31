package com.gitub.thorbenkuck.tears.client.ui.server.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.messages.*;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.network.Client;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.session.gm.view.GMView;
import com.gitub.thorbenkuck.tears.client.ui.session.player.view.PlayerView;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;
import com.google.common.eventbus.Subscribe;

import java.util.Observable;
import java.util.Observer;

class ServerPresenterImpl implements ServerPresenter {

	private final Repository repository;
	private final Client client;
	private final Observer observer = new GameSessionObserver();
	private final MetaController metaController;
	private ServerView serverView;

	ServerPresenterImpl(Repository repository, MetaController metaController) {
		this.repository = repository;
		client = repository.get(Client.class);
		this.metaController = metaController;
	}

	private void updateSessionList(GameSessionListUpdate update) {
		if (update != null) {
			serverView.setSessionList(update.getGameSessions());
		}
	}

	@Override
	public <T extends View> void inject(T view) {
		serverView = (ServerView) view;
	}

	@Override
	public ServerView getView() {
		return serverView;
	}

	@Override
	public void createNewSession(String sessionName) {
		client.send(new CreateSessionRequest(sessionName));
	}

	@Override
	public void join(GameSession gameSession) {
		client.send(new JoinGameSessionRequest(gameSession));
	}

	@Override
	public void cleanUp() {
		repository.deleteObserver(observer);
		serverView = null;
	}

	@Subscribe
	private void receive(CreateSessionResponse createSessionResponse) {
		if(createSessionResponse.isSuccessful()) {
			serverView.setFeedback("Die Session " + createSessionResponse.getGameSession().getName() + " wurde erstellt!");
			repository.add(createSessionResponse.getGameSession());
			GMView gmView = metaController.show(GMView.class);
			gmView.getPresenter().setUser(repository.get(User.class));
		} else {
			serverView.setFeedback(createSessionResponse.getMessage());
		}
	}

	@Subscribe
	private void handle(JoinGameSessionResponse response) {
		if(response.isSuccessful()) {
			Logger.debug("You are: " + repository.get(User.class));
			repository.add(response.getGameSession());
			PlayerView playerView = metaController.show(PlayerView.class);
			playerView.getPresenter().setUser(repository.get(User.class));
		} else {
			serverView.setFeedback("Konnte nicht beitreten");
		}
	}

	@Override
	public void afterDisplay() {
		serverView.setTitle(repository.get(User.class).getUserName() + "@" + client.getAddress());
		try {
			updateSessionList(repository.get(GameSessionListUpdate.class));
		} catch(NullPointerException ignored) {
			// this exception means, that
			// there is not GameSessionListUpdate
		}
	}

	@Override
	public void setup() {
		repository.addObserver(observer);
	}

	private class GameSessionObserver implements Observer {

		@Override
		public void update(Observable o, Object arg) {
			if (arg != null && arg.getClass().equals(GameSessionListUpdate.class)) {
				updateSessionList((GameSessionListUpdate) arg);
			}
		}
	}
}
