package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public class CloseEventHandler implements EventHandler<WindowEvent> {
	@Override
	public void handle(WindowEvent event) {
		event.consume();
		Logger.info("Closing Application");
		System.exit(0);
	}
}
