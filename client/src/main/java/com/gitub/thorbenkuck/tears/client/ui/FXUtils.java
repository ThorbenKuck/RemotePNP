package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import javafx.application.Platform;

public class FXUtils {

	public static void runOnApplicationThread(Runnable runnable) {
		Logger.debug("Executing runnable on ApplicationThread");
		if(Platform.isFxApplicationThread()) {
			Logger.trace("On ApplicationThread. Executing now..");
			runnable.run();
		} else {
			Logger.trace("Not on ApplicationThread. Appending runnable to work queue");
			Platform.runLater(runnable);
		}
	}

}
