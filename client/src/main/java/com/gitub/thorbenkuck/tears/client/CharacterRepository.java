package com.gitub.thorbenkuck.tears.client;

import com.github.thorbenkuck.tears.shared.Settings;
import com.github.thorbenkuck.tears.shared.datatypes.Character;
import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CharacterRepository extends Observable {

	private final List<Character> characterList = new ArrayList<>();
	private final Map<Character, String> knownFileStorage = new HashMap<>();
	private final Settings settings;

	public CharacterRepository(Settings settings) {
		this.settings = settings;
	}

	private boolean writeToFile(Character character, String path) {
		try {
			FileOutputStream fileIn = new FileOutputStream(path);
			ObjectOutputStream objectIn = new ObjectOutputStream(fileIn);
			objectIn.writeObject(character);
			objectIn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private Object readObjectFromFile(String filePath) {
		try {
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			Object obj = objectIn.readObject();
			objectIn.close();
			return obj;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean knownStorage(Character character) {
		synchronized (knownFileStorage) {
			return knownFileStorage.get(character) != null;
		}
	}

	public boolean updateStoredVersion(Character character) {
		String s;
		synchronized (knownFileStorage) {
			s = knownFileStorage.get(character);
		}

		if(s == null) {
			return false;
		}

		return writeToFile(character, s);
	}

	public void setKnownFileStorage(Character character, String file) {
		synchronized (knownFileStorage) {
			knownFileStorage.put(character, file);
		}

		if(!settings.getCharacterPaths().contains(file)) {
			settings.appendCharacterPath(file);
			settings.store();
		}
	}

	public void mapToFile(Character character, String file) {
		setKnownFileStorage(character, file);
		if(!settings.getCharacterPaths().contains(file)) {
			settings.appendCharacterPath(file);
		}
	}

	public void add(Character character) {
		setChanged();
		Logger.debug("Adding " + character + " to repository");
		synchronized (characterList) {
			if(!characterList.contains(character)) {
				characterList.add(character);
			}
		}
		notifyObservers();
	}

	public List<Character> getAll() {
		return new ArrayList<>(characterList);
	}

	/**
	 * Returns true if:
	 *
	 * - The path is a file and is readable by an ObjectInputStream.
	 * - The path is a folder and no IOException happens while reading the contents
	 *
	 * @param path the path to search fo
	 * @return true, if the character is loaded, else false
	 */
	public boolean load(String path) {
		File file = new File(path).getAbsoluteFile();
		Logger.trace("Trying to load: " + file.getAbsolutePath());
		if(!file.exists()) {
			return false;
		}

		if(file.isDirectory()) {
			Logger.trace("Folder detected. Searching " + file.toPath() + " recursively.");
			for(File subFile : file.listFiles()) {
				Logger.trace("Looking at " + subFile.getPath());
				if(subFile.isFile() && subFile.getName().endsWith(".character")) {
					load(subFile.getAbsoluteFile().getPath());
				}
			}

			return true;
		} else if(file.isFile()) {
			Object object = readObjectFromFile(path);
			if (object == null || !Character.class.equals(object.getClass())) {
				return false;
			}

			Character character = (Character) object;

			if(character.getNotes() != null) {
				System.err.println("Found old format character!");
				character.shiftToNewFormat();
				System.out.println(character.getRepository());
				if(character.getRepository().isEmpty() || character.getNotes() != null) {
					throw new IllegalStateException("PANIC");
				}
				writeToFile(character, path);
			}

			add(character);
			setKnownFileStorage(character, path);
			return true;
		}

		return false;
	}

	public boolean storeAndAdd(Character character, String path) {
		if (writeToFile(character, path)) {
			add(character);
			mapToFile(character, path);
			return true;
		}

		return false;
	}


}
