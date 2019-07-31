package com.gitub.thorbenkuck.tears.client.ui.chat;

import com.gitub.thorbenkuck.tears.client.ui.session.Message;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class OwnChatMessage extends HBox {

	private final Color fill = Color.LIGHTGREEN;

	public OwnChatMessage(String message, ListCell<Message> customListCell) {
		super(0);
		Polygon triangle = new Polygon();
		triangle.getPoints()
				.addAll(0.0, 0.0,
						14.0, 14.0,
						14.0, 0.0);
		triangle.setFill(fill);
		triangle.setStroke(fill);

		StackPane stackPane = new StackPane();
		Label label = new Label(message);
		label.setWrapText(true);
		stackPane.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		label.maxWidthProperty().bind(customListCell.widthProperty().subtract(80));

		stackPane.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px 5px 5px 5px; -fx-border-radius: 0 10 10 10; -fx-background-radius: 0 10 10 10;");
		stackPane.getChildren().addAll(label);

		getChildren().addAll(triangle, stackPane);
	}
}