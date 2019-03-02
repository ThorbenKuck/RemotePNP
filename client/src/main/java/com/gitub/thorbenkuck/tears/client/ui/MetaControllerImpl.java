package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
class MetaControllerImpl implements MetaController {

	private final Map<Class<? extends View>, Supplier<Presenter>> presenterFactoryMapping = new HashMap<>();
	private final Map<Class<? extends View>, Function<Presenter, View>> viewFactoryMapping = new HashMap<>();
	private final AtomicReference<View> currentView = new AtomicReference<>();
	private final AtomicReference<Stage> mainStage = new AtomicReference<>();
	private final EventBus eventBus;

	MetaControllerImpl(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	private void close(View view) {
		Logger.debug("Closing the view " + view.getClass().getSimpleName());
		Presenter presenter = view.getPresenter();
		presenter.destroy();
		Logger.debug("Unregistering Presenter from EventBus");
		eventBus.unregister(presenter);
		Logger.info("Closed " + view.getClass().getSimpleName());
	}

	private <T extends View> T construct(Class<T> type, Stage stage) {
		Logger.debug("Attempting to construct " + type);
		Supplier<Presenter> presenterSupplier;
		Function<Presenter, View> viewFactory;
		synchronized (presenterFactoryMapping) {
			synchronized (viewFactoryMapping) {
				presenterSupplier = presenterFactoryMapping.get(type);
				viewFactory = viewFactoryMapping.get(type);
			}
		}

		if (presenterSupplier == null || viewFactory == null) {
			throw new IllegalArgumentException("The view " + type + " is not registered!");
		}

		Presenter presenter;
		View view;

		// This try catch is sadly needed.
		// Since the java generics are very
		// limited, we can only hope, the user
		// gave us the right combination
		try {
			Logger.debug("Constructing Presenter..");
			presenter = presenterSupplier.get();
			Logger.debug("Constructing View..");
			view = viewFactory.apply(presenter);
			Logger.debug("Injecting View into Presenter..");
			presenter.inject(view);
			view.setStage(stage);
		} catch (Exception e) {
			throw new IllegalStateException("Unsupported view types supplied for the view type: " + type, e);
		}

		Logger.debug("Construction successfully finished..");
		return (T) view;
	}

	private <T extends View> T createAndShowNewView(Class<T> type, Stage stage) {
		T view = construct(type, stage);
		Logger.debug("Displaying the view " + view.getClass().getSimpleName());
		Presenter presenter = view.getPresenter();

		try {
			Logger.trace("Setting up View");
			view.setup();
			Logger.trace("Setting up Presenter");
			presenter.setup();
			Logger.trace("Registering Presenter to EventBus");
			eventBus.register(presenter);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}

		try {
			Logger.trace("Informing Presenter to display View");
			presenter.display();
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
		Logger.debug("View displayed. Informing presenter");

		return view;
	}

	private Stage createStage() {
		if (Platform.isFxApplicationThread()) {
			return new Stage();
		} else {
			Logger.debug("Not on ApplicationThread. Awaiting ApplicationThread");
			CompletableFuture<Stage> stageFuture = new CompletableFuture<>();
			FXUtils.runOnApplicationThread(() -> stageFuture.complete(new Stage()));

			try {
				return stageFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public <T extends View> T showSeparate(Class<T> type) {
		Objects.requireNonNull(type, "ViewType must not be null!");
		return createAndShowNewView(type, createStage());
	}

	@Override
	public <T extends View> void show(Class<T> type) {
		FXUtils.runOnApplicationThread(() -> {
			Objects.requireNonNull(type, "ViewType must not be null!");
			if(mainStage.get() == null) {
				Logger.debug("No main stage set. Creating stage");
				createNewMainStage();
			}
			handleCurrentView();
			View view = createAndShowNewView(type, mainStage.get());
			setCurrentView(view);
		});
	}

	private void handleCurrentView() {
		View outdated = currentView.get();
		if (outdated != null) {
			close(outdated);
		}

		currentView.set(null);
	}

	private void setCurrentView(View view) {
		currentView.set(view);
	}

	@Override
	public <T extends View> void register(Class<T> type, Supplier<Presenter> presenterFactory, Function<Presenter, View> viewFactory) {
		synchronized (presenterFactoryMapping) {
			synchronized (viewFactoryMapping) {
				presenterFactoryMapping.put(type, presenterFactory);
				viewFactoryMapping.put(type, viewFactory);
			}
		}
	}

	@Override
	public void createNewMainStage() {
		final Stage stage = createStage();
		setMainStage(stage);
	}

	@Override
	public void setMainStage(Stage stage) {
		mainStage.set(stage);
	}

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public Class<? extends View> getOpenWindowType() {
		return Optional.ofNullable(currentView.get()).orElse(NULL_VIEW).getClass();
	}

	private static final View NULL_VIEW = new NullView();

	private static class NullView implements View {

		@Override
		public Presenter getPresenter() {
			return null;
		}

		@Override
		public void close() {

		}

		@Override
		public void setup() {

		}

		@Override
		public void display() {

		}

		@Override
		public void setStage(Stage stage) {

		}
	}
}
