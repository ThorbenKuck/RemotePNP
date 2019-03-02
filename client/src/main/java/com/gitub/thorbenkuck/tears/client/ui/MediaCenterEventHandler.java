package com.gitub.thorbenkuck.tears.client.ui;

import com.gitub.thorbenkuck.tears.client.media.MediaCenter;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.function.Consumer;

public class MediaCenterEventHandler implements EventHandler<KeyEvent> {

	private final Consumer<String> callback;
	private final Runnable killSoundsCallback;

	public MediaCenterEventHandler(Consumer<String> callback, Runnable killSoundsCallback) {
		this.callback = callback;
		this.killSoundsCallback = killSoundsCallback;
	}

	@Override
	public void handle(KeyEvent keyEvent) {
		if(keyEvent.isControlDown()) {
			if(keyEvent.getCode() == KeyCode.M) {
				MediaCenter.changeMute();
				if (MediaCenter.isMuted()) {
					callback.accept("Sound deactivated");
				} else {
					callback.accept("Sound activated");
				}
			} else if(keyEvent.getCode() == KeyCode.PLUS) {
				if(MediaCenter.higherVolume()) {
					callback.accept("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
				}
			} else if(keyEvent.getCode() == KeyCode.MINUS) {
				if(MediaCenter.lowerVolume()) {
					callback.accept("Volume changed to: " + (int) (MediaCenter.getVolume() * 100) + "%");
				}
			} else if(keyEvent.getCode() == KeyCode.K) {
				callback.accept("Musik wiedergabe beendet");
				killSoundsCallback.run();
			}
		}
	}
}
