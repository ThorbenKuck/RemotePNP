package com.gitub.thorbenkuck.tears.client.ui.server.presenter;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.gitub.thorbenkuck.tears.client.Repository;
import com.gitub.thorbenkuck.tears.client.ui.MetaController;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.server.view.ServerView;

public interface ServerPresenter extends Presenter {

	static ServerPresenter create(Repository repository, MetaController metaController) {
		return new ServerPresenterImpl(repository, metaController);
	}

	ServerView getView();

	void createNewSession(String sessionName);

	void join(GameSession gameSession);
}
