package com.gitub.thorbenkuck.tears.client.media;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.google.common.util.concurrent.AtomicDouble;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MediaCenter {

	private static final List<MediaPlayer> playerList = new ArrayList<>();
	private static final AtomicBoolean muted = new AtomicBoolean(false);
	private static final AtomicReference<BigDecimal> volume = new AtomicReference<>(BigDecimal.valueOf(0.3));

	private static void setAllTo(double volume) {
		synchronized (playerList) {
			playerList.forEach(mediaPlayer -> mediaPlayer.setVolume(volume));
		}
		Logger.debug("Updated volume of all media players to " + volume);
	}

	public static void killAll() {
		synchronized (playerList) {
			playerList.forEach(MediaPlayer::stop);
		}
		playerList.clear();
	}

	public synchronized static void changeMute() {
		muted.set(!muted.get());
		if (muted.get()) {
			setAllTo(0.0);
		} else {
			updateRunningMedia();
		}
	}

	public static boolean higherVolume() {
		if (isMuted()) {
			changeMute();
		}
		BigDecimal current = volume.get();
		BigDecimal newValue;

		if(current.doubleValue() < 0.2) {
			newValue = current.add(BigDecimal.valueOf(0.01));
		} else {
			newValue = current.add(BigDecimal.valueOf(0.1));
		}

		if (newValue.doubleValue() > 1.0) {
			newValue = BigDecimal.valueOf(1.0);
		}

		volume.set(newValue);
		updateRunningMedia();
		return !current.equals(newValue);
	}

	public static boolean lowerVolume() {
		if (isMuted()) {
			changeMute();
		}
		BigDecimal current = volume.get();
		BigDecimal newValue;

		if(current.doubleValue() <= 0.2) {
			newValue = current.subtract(BigDecimal.valueOf(0.01));
		} else {
			newValue = current.subtract(BigDecimal.valueOf(0.1));
		}

		if (newValue.doubleValue() < 0.0) {
			newValue = BigDecimal.valueOf(0.0);
		}

		volume.set(newValue);
		updateRunningMedia();
		return !current.equals(newValue);
	}

	public static void updateRunningMedia() {
		setAllTo(getVolume());
	}

	public static double getVolume() {
		return volume.get().doubleValue();
	}

	public static void setVolume(double to) {
		volume.set(BigDecimal.valueOf(to));
	}

	public static boolean isMuted() {
		return muted.get();
	}

	private static boolean playRelative(String name) {
		URL url = MediaCenter.class.getResource("/sounds/" + name);

		if (url == null) {
			return false;
		}

		try {
			Media media = new Media(MediaCenter.class.getResource("/sounds/" + name).toURI().toString());
			play(media);
			return true;
		} catch (URISyntaxException e) {
			Logger.catching(e);
		}

		return false;
	}

	private static boolean playAbsolute(String name) {
		File file = new File(name);
		if(!file.exists()) {
			return false;
		}
		String path = file.toURI().toString();
		Logger.debug("Playing " + path);
		Media media = new Media(path);
		play(media);

		return true;
	}

	public static boolean playSound(String name) {
		if (isMuted()) {
			Logger.info("Ignored playing: " + name);
			return false;
		}

		if(name.startsWith("absolute:")) {
			name = name.substring(9);
			return playAbsolute(name);
		} else {
			return playRelative(name);
		}
	}

	public static void playDiceRoll() {
		int number = ThreadLocalRandom.current().nextInt(3);
		playSound("dice_roll_short" + number + ".mp3");
	}

	public static void playDing() {
		playSound("ding.mp3");
	}

	public static void playImageDisplayed() {
		playSound("image_display.mp3");
	}

	public static void playChatMessage() {
		playSound("chat_message.mp3");
	}

	public static void playDiDo() {
		playSound("di_do.mp3");
	}

	public static void playDoDi() {
		playSound("do_di.mp3");
	}

	private static void play(Media media) {
		try {
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setOnEndOfMedia(() -> {
				mediaPlayer.dispose();
				synchronized (playerList) {
					playerList.remove(mediaPlayer);
				}
			});

			mediaPlayer.setVolume(getVolume());

			synchronized (playerList) {
				playerList.add(mediaPlayer);
			}

			mediaPlayer.play();
		} catch (Exception e) {
			Logger.catching(e);
		}
	}
}
