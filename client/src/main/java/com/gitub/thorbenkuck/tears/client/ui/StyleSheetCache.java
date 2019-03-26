package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class StyleSheetCache {

	public static final String SETUP_VIEW = "setup";
	public static final String SERVER_VIEW = "main_menu";
	public static final String GM_VIEW = "gm";
	public static final String PLAYER_VIEW = "player";
	private static final Map<String, String> sheetPaths = new HashMap<>();

	private static final boolean debugMode = true;

	static {
		if(debugMode) {
			setFromResources();
		} else {
			Logger.debug("Trying to access external styles folder ..");
			boolean set = false;
			File externalRoot = new File("styles");
			if (!externalRoot.exists() || !externalRoot.isDirectory()) {
				Logger.debug("External styles folder does not exist. Trying to create it ..");
				if (!externalRoot.mkdir()) {
					Logger.error("Could not create the style root folder!");
					setFromResources();
					set = true;
				}
			}

			if (!set) {
				Logger.debug("Loading css from external folder");
				load();
			}
			Logger.debug("Informing " + ImageCache.class.getSimpleName() + " ..");
			ImageCache.load();
		}
	}

	private StyleSheetCache() {
	}

	private static void load() {

		File externalRoot = new File("styles");
		InputStream inputStream = StyleSheetCache.class.getClassLoader().getResourceAsStream("styles/");

		if (inputStream == null) {
			throw new IllegalStateException("Could not locate internal style folder!");
		} else {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			if(!externalRoot.exists()) {
				setFromResources();
				return;
			}
			List<String> fileNames = Arrays.stream(Objects.requireNonNull(externalRoot.listFiles()))
					.filter(File::isFile)
					.filter(file -> file.getName().endsWith(".css"))
					.map(File::getName)
					.collect(Collectors.toList());
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					Logger.debug("Checking " + line);
					if (!fileNames.contains(line)) {
						Logger.debug("Copping file");
						File temp = new File(externalRoot, line);
						try {
							Files.copy(StyleSheetCache.class.getClassLoader().getResourceAsStream("styles/" + line), temp.toPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			setFromExternalFolder(externalRoot);
		}
	}

	private static void setFromExternalFolder(File folder) {
		sheetPaths.put(SETUP_VIEW, "file:///" + new File(folder, "setup_view.css").getAbsolutePath());
		sheetPaths.put(SERVER_VIEW, "file:///" + new File(folder, "server_view.css").getAbsolutePath());
		sheetPaths.put(GM_VIEW, "file:///" + new File(folder, "gm_view.css").getAbsolutePath());
		sheetPaths.put(PLAYER_VIEW, "file:///" + new File(folder, "player_view.css").getAbsolutePath());
	}

	private static void setFromResources() {
		Logger.warn("Using fallback from resources!");
		sheetPaths.put(SETUP_VIEW, StyleSheetCache.class.getClassLoader().getResource("styles/setup_view.css").toExternalForm());
		sheetPaths.put(SERVER_VIEW, StyleSheetCache.class.getClassLoader().getResource("styles/server_view.css").toExternalForm());
		sheetPaths.put(GM_VIEW, StyleSheetCache.class.getClassLoader().getResource("styles/gm_view.css").toExternalForm());
		sheetPaths.put(PLAYER_VIEW, StyleSheetCache.class.getClassLoader().getResource("styles/player_view.css").toExternalForm());
	}

	public static String pathToSetup() {
		return sheetPaths.get(SETUP_VIEW);
	}

	public static String pathToMainMenu() {
		return sheetPaths.get(SERVER_VIEW);
	}

	public static String pathToGMView() {
		return sheetPaths.get(GM_VIEW);
	}

	public static String pathToPlayerView() {
		return sheetPaths.get(PLAYER_VIEW);
	}
}
