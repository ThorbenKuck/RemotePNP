package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import com.github.thorbenkuck.tears.shared.SoundDownloadAndMapRequest;
import com.gitub.thorbenkuck.tears.client.media.MediaCenter;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SoundBoard {

	private static final SoundBoard INSTANCE = new SoundBoard();
	private static final String SPLIT_CHAR = ":";
	private final Map<String, String> mapping = new HashMap<>();
	private final List<String> fileNames = new ArrayList<>();
	private final File customSoundList = new File("soundlist.config");
	private final File customSoundFolder = new File("sounds");

	private SoundBoard() {
		load();
		loadCustom();
	}

	private void load() {
		InputStream inputStream = SoundBoard.class.getClassLoader().getResourceAsStream("soundlist");
		if(inputStream == null) {
			Logger.error("No soundlist found in resources!");
		} else {
			read(inputStream);
		}
	}

	private void read(InputStream inputStream) {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			String current = bufferedReader.readLine();
			while(current != null) {
				if(!current.isEmpty()) {
					String[] content;
					if (current.contains(SPLIT_CHAR)) {
						content = current.split(SPLIT_CHAR, 2);
						if(content.length != 2) {
							Logger.warn("Illegal split: " + current);
						} else {
							integrate(content[0], content[1]);
						}
					} else {
						Logger.warn("Illegal entry in soundlist: " + current);
					}
				}
				current = bufferedReader.readLine();
			}
		} catch (IOException e) {
			Logger.catching(e);
		}
	}

	private void loadCustom() {
		if(!customSoundFolder.exists() || !customSoundFolder.isDirectory()) {
			if(!customSoundFolder.mkdir()) {
				Logger.warn("Could not create the sound folder!");
				return;
			}
		}
		if(!customSoundList.exists()) {
			try {
				if(!customSoundList.createNewFile()) {
					Logger.warn("Could not create the file soundlist.config!");
					return;
				}
			} catch (IOException e) {
				Logger.catching(e);
			}
		}

		try(FileInputStream fileInputStream = new FileInputStream(customSoundList)) {
			read(fileInputStream);
		} catch (IOException e) {
			Logger.catching(e);
		}
	}

	private void setIntoCustom(String callName, String filename) {
		try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(customSoundList, true))) {
			bufferedWriter.write(callName + SPLIT_CHAR + "absolute:" + filename);
			bufferedWriter.write(System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean integrate(SoundDownloadAndMapRequest request) {
		File target = new File(customSoundFolder.getAbsoluteFile() + File.separator + request.getFilename());
		if(!target.exists()) {
			try {
				if(!target.createNewFile()) {
					Logger.warn("Could not create the requested file!");
					return false;
				}
			} catch (IOException e) {
				Logger.catching(e);
				Logger.warn("Could not create the requested file!");
				return false;
			}
		}
		try(FileOutputStream fileOutputStream = new FileOutputStream(target)) {
			fileOutputStream.write(request.getData());
			Logger.info("Introduced new sound: " + target.getAbsolutePath());
			integrate(request.getMapName(), "absolute:" + target.getAbsolutePath());
			setIntoCustom(request.getMapName(), target.getAbsolutePath());
			return true;
		} catch (IOException e) {
			Logger.catching(e);
			return false;
		}
	}

	public Sound find(String callName) {
		String path;
		synchronized (mapping) {
			path = mapping.get(callName);
		}

		Logger.debug("Path from mapping: " + path);
		Logger.debug("Mapping: " + mapping);

		if(path == null && getFileNames().contains(callName)) {
			Logger.warn("Sound could not be found. Returning the callName");
			return new Sound(callName, callName);
		}

		return new Sound(callName, path);
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public List<Sound> createSoundList() {
		return mapping.entrySet()
				.stream()
				.map(set -> new Sound(set.getKey(), set.getValue()))
				.sorted(Comparator.comparing(Sound::getName))
				.collect(Collectors.toList());
	}

	public void integrate(String callName, String fileName) {
		synchronized (mapping) {
			mapping.put(callName, fileName);
		}
		synchronized (fileNames) {
			fileNames.add(fileName);
		}
	}

	public boolean playSound(Sound sound) {
		String path = sound.getPath();
		return MediaCenter.playSound(path);
	}

	public static Sound handleMapping(String raw) {
		String[] parts = raw.split(SPLIT_CHAR, 2);
		if(parts.length != 2) {
			return null;
		}

		SoundBoard soundBoard = SoundBoard.getInstance();
		Sound sound = soundBoard.find(parts[0]);
		soundBoard.integrate(sound.getName(), "absolute:" + parts[1]);
		sound = soundBoard.find(sound.getName());
		return sound;
	}

	public static SoundBoard getInstance(){
		return INSTANCE;
	}

	public void customMapping(File file, String name) {
		integrate(name, "absolute:" + file.getAbsolutePath());
		setIntoCustom(name, file.getAbsolutePath());
	}
}
