package com.gitub.thorbenkuck.tears.client.ui.session.gm.view;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class ImageHandler {

	byte[] imageToByteArray(Image image) throws IOException {
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, "jpg", outputStream);
			return outputStream.toByteArray();
		}
	}

	byte[] readImageFile(Stage toShowDialogOn) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif");
		fileChooser.getExtensionFilters().add(extFilter);

		File selected = fileChooser.showOpenDialog(toShowDialogOn);
		try {
			return Files.readAllBytes(selected.toPath());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
