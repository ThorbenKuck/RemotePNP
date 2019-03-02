package com.gitub.thorbenkuck.tears.client.ui.server.view;

import com.github.thorbenkuck.tears.shared.datatypes.GameSession;
import com.gitub.thorbenkuck.tears.client.ui.Presenter;
import com.gitub.thorbenkuck.tears.client.ui.View;
import com.gitub.thorbenkuck.tears.client.ui.server.presenter.ServerPresenter;

import java.util.List;

public interface ServerView extends View {

	static ServerView create(Presenter presenter) {
		return new JavaFXServerView((ServerPresenter) presenter);
	}

	void setFeedback(String message);

	void setErrorFeedback(String message);

	ServerPresenter getPresenter();

	void setSessionList(List<GameSession> gameSessions);

	void setTitle(String title);
}
