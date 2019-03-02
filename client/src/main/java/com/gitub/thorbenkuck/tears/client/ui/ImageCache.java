package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImageCache {

	public static void load() {
		Logger.debug("Trying to access external images folder ..");
		File externalRoot = new File("images");
		if (!externalRoot.exists() || !externalRoot.isDirectory()) {
			Logger.debug("External images folder does not exist. Trying to create it ..");
			if (!externalRoot.mkdir()) {
				throw new IllegalStateException("Could not create the images root folder!");
			}
		}

		Logger.debug("Transferring images from external folder");
		URL resourcesUrl = StyleSheetCache.class.getClassLoader().getResource("images/");

		if (resourcesUrl == null) {
			throw new IllegalStateException("Could not locate internal images folder!");
		} else {
			List<String> fileNames = Arrays.stream(Objects.requireNonNull(externalRoot.listFiles()))
					.filter(File::isFile)
					.filter(file -> file.getName().endsWith(".jpg"))
					.map(File::getName)
					.collect(Collectors.toList());
			try {
				File resourceFolder = new File(resourcesUrl.toURI());
				for (File resource : Objects.requireNonNull(resourceFolder.listFiles())) {
					if (!fileNames.contains(resource.getName())) {
						File temp = new File(externalRoot, resource.getName());
						try {
							Files.copy(resource.toPath(), temp.toPath());
						} catch (IOException ignored) {
						}
					}
				}
			} catch (URISyntaxException ignored) {
				// will not happen..
			}
		}
	}

}
