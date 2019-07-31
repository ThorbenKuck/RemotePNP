package com.github.thorbenkuck;

import com.github.thorbenkuck.tears.shared.Settings;
import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.github.thorbenkuck.tears.shared.datatypes.User;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.network.Client;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.session.player.presenter.PlayerPresenter;
import com.gitub.thorbenkuck.tears.client.ui.session.player.view.PlayerView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

public class TestMain extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final User me = new User("Test2", new Character("Bar", new ArrayList<>(), new ArrayList<>(), 1, 1));
		me.getCharacter().getAttributeList().add(new Attribute("foo", 100));
		final User other = new User("Test1", new Character("Foo", new ArrayList<>(), new ArrayList<>(), 1, 1));
		final GameSession gameSession = new GameSession("Foo", new ArrayList<>(Collections.singletonList(me)), other);

		Repository repository = new Repository();
		repository.add(Client.class, new ClientMock());
		PlayerPresenter playerPresenter = PlayerPresenter.create(repository, new CharacterRepository(new Settings()), MetaController.create());
		PlayerView playerView = PlayerView.create(playerPresenter);
		playerPresenter.inject(playerView);
		playerPresenter.setUser(me);
		playerPresenter.setGameSession(gameSession);
		playerView.setStage(primaryStage);

		playerPresenter.display();
		playerPresenter.setParticipants(gameSession.getParticipants());
	}
}
