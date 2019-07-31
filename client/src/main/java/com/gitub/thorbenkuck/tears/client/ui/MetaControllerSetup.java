package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.Settings;
import com.github.thorbenkuck.tears.shared.messages.*;
import com.gitub.thorbenkuck.tears.client.CharacterRepository;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.media.MediaCenter;
import com.gitub.thorbenkuck.tears.client.ui.character.presenter.CharacterPresenter;
import com.gitub.thorbenkuck.tears.client.ui.character.view.CharacterView;
import com.gitub.thorbenkuck.tears.client.ui.session.gm.presenter.GMPresenter;
import com.gitub.thorbenkuck.tears.client.ui.session.gm.view.GMView;
import com.gitub.thorbenkuck.tears.client.ui.notes.presenter.NotesPresenter;
import com.gitub.thorbenkuck.tears.client.ui.notes.view.NotesView;
import com.gitub.thorbenkuck.tears.client.ui.session.player.presenter.PlayerPresenter;
import com.gitub.thorbenkuck.tears.client.ui.session.player.view.PlayerView;
import com.gitub.thorbenkuck.tears.client.ui.server.presenter.ServerPresenter;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;
import com.gitub.thorbenkuck.tears.client.ui.setup.presenter.SetupPresenter;
import com.gitub.thorbenkuck.tears.client.ui.setup.view.SetupView;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;

class MetaControllerSetup {

	static void load(MetaController metaController) {
		EventBus eventBus = metaController.getEventBus();
		Repository repository = new Repository();
		Settings settings = new Settings();

		CharacterRepository characterRepository = new CharacterRepository(settings);
		eventBus.register(new Subscriber(repository, metaController));

		metaController.register(SetupView.class, () -> SetupPresenter.create(metaController, characterRepository, repository, eventBus, settings), SetupView::create);
		metaController.register(CharacterView.class, () -> CharacterPresenter.create(characterRepository, metaController), CharacterView::create);
		metaController.register(ServerView.class, () -> ServerPresenter.create(repository, metaController), ServerView::create);
		metaController.register(GMView.class, () -> GMPresenter.create(repository, characterRepository, metaController), GMView::create);
		metaController.register(PlayerView.class, () -> PlayerPresenter.create(repository, characterRepository, metaController), PlayerView::create);
		metaController.register(NotesView.class, () -> NotesPresenter.create(repository), NotesView::create);
	}

	private static class Subscriber {

		private final Repository repository;
		private final MetaController metaController;

		private Subscriber(Repository repository, MetaController metaController) {
			this.repository = repository;
			this.metaController = metaController;
		}

		@Subscribe
		private void handle(LeaveGameSessionResponse response) {
			metaController.show(ServerView.class);
		}

		@Subscribe
		private void handle(DisplayNotes displayNotes) {
			FXUtils.runOnApplicationThread(() -> {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setResizable(true);

				alert.setTitle("Notizen Ansicht");
				alert.setHeaderText("Du siehst die Notizen von: " + displayNotes.getOrigin().getUserName());
				alert.setContentText("Diese Notizen sind die Veröffentlich worden. Du kannst sie nicht anpassen!");

				WebView webView = Markdown.toWebView(displayNotes.getNotes());
				webView.setMaxWidth(Double.MAX_VALUE);
				webView.setMaxHeight(Double.MAX_VALUE);
				GridPane.setVgrow(webView, Priority.ALWAYS);
				GridPane.setHgrow(webView, Priority.ALWAYS);

				GridPane expContent = new GridPane();
				expContent.setMaxWidth(Double.MAX_VALUE);
				expContent.add(webView, 0, 0);

// Set expandable Exception into the dialog pane.
				alert.getDialogPane().setContent(expContent);

				MediaCenter.playDing();
				alert.showAndWait();
			});
		}
	}

}
