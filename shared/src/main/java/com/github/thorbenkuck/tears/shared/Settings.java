package com.github.thorbenkuck.tears.shared;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Settings {

	private static final int DEFAULT_INT = -1;
	private static final double DEFAULT_DOUBLE = -1.0;
	private static final String DEFAULT_STRING = "null";
	private final Properties properties = new Properties();
	private final File target;

	public Settings() {
		URL url = Settings.class.getProtectionDomain().getCodeSource().getLocation();
		try {
			File folder = new File(url.toURI()).getParentFile();
			target = new File(folder.getPath() + "/configuration.properties");
			Logger.debug("Configuration target: " + target.getPath());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private String tryGet(String key) {
		String prop = properties.getProperty(key);
		if(prop == null) {
			return System.getProperty(key);
		} else {
			return prop;
		}
	}

	private void checkTarget() {
		if (!target.exists()) {
			Logger.debug("Creating configuration file");
			try {
				if (!target.createNewFile()) {
					throw new IllegalStateException("Could not create the configuration file");
				}
				Logger.trace("Configuration file created. Setting default properties.");

				Properties defaultProperties = new Properties();
				String resourceName = "default.properties";
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
					defaultProperties.load(resourceStream);
				}
				Logger.debug(target.getAbsoluteFile().getParent());
				defaultProperties.setProperty("character.paths", target.getAbsoluteFile().getParent());
				store(defaultProperties, target);
			} catch (IOException e) {
				Logger.catching(e);
				throw new IllegalStateException(e);
			}
		}
	}

	private void store(Properties properties, File target) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(target)) {
			properties.store(fileOutputStream, "Auto generated file");
		} catch (IOException e) {
			Logger.catching(e);
		}
	}

	public void load() {
		Logger.debug("Loading properties from " + target.getPath());
		checkTarget();
		try (FileInputStream fileInputStream = new FileInputStream(target)) {
			properties.load(fileInputStream);
		} catch (IOException e) {
			Logger.catching(e);
		}
	}

	public void store() {
		Logger.debug("Storing properties to " + target.getPath());
		checkTarget();
		store(properties, target);
	}

	public boolean getBoolean(String key) {
		String prop = tryGet(key);
		return Boolean.parseBoolean(prop);
	}

	public int getInt(String key) {
		return getInt(key, DEFAULT_INT);
	}

	public int getInt(String key, int defaultValue) {
		String prop = tryGet(key);
		try {
			return Integer.parseInt(prop);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public long getLong(String key) {
		String prop = tryGet(key);
		try {
			return Long.parseLong(prop);
		} catch (NumberFormatException e) {
			return DEFAULT_INT;
		}
	}

	public double getDouble(String key) {
		String prop = tryGet(key);
		try {
			return Double.parseDouble(prop);
		} catch (NumberFormatException e) {
			return DEFAULT_DOUBLE;
		}
	}

	public String get(String key) {
		return get(key, DEFAULT_STRING);
	}

	public String get(String key, String defaultValue) {
		String prop = tryGet(key);
		if (prop == null) {
			return defaultValue;
		} else {
			return prop;
		}
	}

	public void appendCharacterPath(String file) {
		Logger.debug("Storing current state to " + file);
		String paths = properties.getProperty("character.paths");
		if (paths == null || paths.isEmpty()) {
			properties.setProperty("character.paths", file);
		} else {
			paths += "," + file;
			properties.setProperty("character.paths", paths);
		}

		store();
	}

	public List<String> getCharacterPaths() {
		String paths = properties.getProperty("character.paths");
		if (paths != null && !paths.isEmpty()) {
			return new ArrayList<>(Arrays.asList(paths.split(","))).stream()
					.filter(p -> !p.isEmpty())
					.collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public void setCharacterPaths(List<String> valid) {
		properties.setProperty("character.paths", "");
		valid.forEach(this::appendCharacterPath);
	}

	public void set(String key, int value) {
		set(key, Integer.toString(value));
	}

	public void set(String key, double value) {
		set(key, Double.toString(value));
	}

	public void set(String key, boolean value) {
		set(key, Boolean.toString(value));
	}

	public void set(String key, String value) {
		properties.setProperty(key, value);
	}
}
