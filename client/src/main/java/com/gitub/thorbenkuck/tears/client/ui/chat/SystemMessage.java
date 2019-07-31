package com.gitub.thorbenkuck.tears.client.ui.chat;

import com.gitub.thorbenkuck.tears.client.ui.session.Message;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

public class SystemMessage extends HBox {

	public SystemMessage(String message, ListCell<Message> customListCell) {
		super(0);

		StackPane stackPane = new StackPane();
		Label label = new Label(message);
		label.setWrapText(true);
		label.maxWidthProperty().bind(customListCell.widthProperty().subtract(80));
		label.setTextAlignment(TextAlignment.CENTER);

		label.setStyle("-fx-text-fill: white;");

		stackPane.setStyle("-fx-background-color: black; -fx-padding: 5px 5px 5px 5px;");
		stackPane.getChildren().addAll(label);
		stackPane.minWidthProperty().bind(customListCell.widthProperty());

		getChildren().add(stackPane);
	}
}
