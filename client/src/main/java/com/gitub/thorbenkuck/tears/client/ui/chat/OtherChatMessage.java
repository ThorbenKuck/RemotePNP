package com.gitub.thorbenkuck.tears.client.ui.chat;

import com.gitub.thorbenkuck.tears.client.ui.session.Message;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class OtherChatMessage extends HBox {

	private final Color fill = Color.LIGHTGRAY;

	public OtherChatMessage(String message, String userName, ListCell<Message> customListCell) {
		super(0);

		Polygon triangle = new Polygon();
		triangle.getPoints()
				.addAll(0.0, 0.0,
						14.0, 14.0,
						14.0, 0.0);
		triangle.setFill(fill);
		triangle.setStroke(fill);

		VBox hBox = new VBox();
		StackPane stackPane = new StackPane();
		Label label = new Label(message);
		label.setWrapText(true);
		label.maxWidthProperty().bind(customListCell.widthProperty().subtract(80));
		stackPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

		stackPane.setStyle("-fx-background-color: lightgray; -fx-padding: 5px 5px 5px 5px;-fx-border-radius: 0 10 10 10;-fx-background-radius: 0 10 10 10;");
		stackPane.getChildren().addAll(label);

		Label username = new Label(userName);
		username.setWrapText(true);
		username.maxWidthProperty().bind(customListCell.widthProperty().subtract(80));

		username.setStyle("-fx-text-fill: rgb(50, 50, 50)");

		hBox.getChildren().addAll(stackPane, username);

		setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		getChildren().addAll(triangle, hBox);
	}

}
