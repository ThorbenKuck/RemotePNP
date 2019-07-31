package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;

public interface Presenter {

	<T extends View> void inject(T view);

	View getView();

	void cleanUp();

	void setup();

	default void afterDisplay() {}

	default void destroy() {
		getView().close();
		cleanUp();
	}

	default void display() {
		setup();
		getView().setup();
		Logger.debug("[PRESENTER] Displaying view");
		getView().display();
		Logger.debug("[PRESENTER] Executing after display");
		afterDisplay();
		Logger.debug("[PRESENTER] Display finished");
	}
}
