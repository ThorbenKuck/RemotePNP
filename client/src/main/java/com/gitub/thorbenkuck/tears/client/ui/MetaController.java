package com.gitub.thorbenkuck.tears.client.ui;

import com.google.common.eventbus.EventBus;
import javafx.stage.Stage;

import java.util.function.Function;
import java.util.function.Supplier;

public interface MetaController {

	static MetaController create() {
		EventBus eventBus = new EventBus();
		MetaController metaController = new MetaControllerImpl(eventBus);
		MetaControllerSetup.load(metaController);

		return metaController;
	}

	<T extends View> T showSeparate(Class<T> type);

	<T extends View> T show(Class<T> type);

	<T extends View> void register(Class<T> type, Supplier<Presenter> presenterFactory, Function<Presenter, View> viewFactory);

	void createNewMainStage();

	void setMainStage(Stage stage);

	EventBus getEventBus();

	Class<? extends View> getOpenWindowType();
}
