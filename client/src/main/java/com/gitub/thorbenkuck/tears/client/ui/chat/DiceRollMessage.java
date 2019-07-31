package com.gitub.thorbenkuck.tears.client.ui.chat;

import com.gitub.thorbenkuck.tears.client.ui.session.Message;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class DiceRollMessage extends HBox {

	public DiceRollMessage(int sides, int result, int amount, String userName, ListCell<Message> customListCell, boolean own) {
		super(0);

		Polygon triangle = new Polygon();
		triangle.getPoints()
				.addAll(0.0, 14.0,
						7.0, 14.0,
						7.0, 0.0);
		Color fill;

		if(own) {
			fill = Color.LIGHTBLUE;
		} else {
			fill = Color.BLUE;
		}

		triangle.setFill(fill);
		triangle.setStroke(fill);

		VBox root = new VBox();

		VBox innerMessage = new VBox();
		Label rolled = new Label(amount + "d" + sides + ":");
		rolled.setWrapText(true);
		rolled.maxWidthProperty().bind(customListCell.widthProperty().subtract(80));
		rolled.setStyle("-fx-font-size: 16px");
		Label resultLabel = new Label(Integer.toString(result));
		resultLabel.setWrapText(true);
		resultLabel.maxWidthProperty().bind(customListCell.widthProperty().subtract(80));
		resultLabel.setStyle("-fx-font-size: 20px");


		if(!own) {
			innerMessage.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
			innerMessage.setStyle("-fx-background-color: blue; -fx-padding: 5px 5px 5px 5px;-fx-border-radius: 0 10 10 10;-fx-background-radius: 0 10 10 10;");
			resultLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
			rolled.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
		} else {
			innerMessage.setStyle("-fx-background-color: lightblue; -fx-padding: 5px 5px 5px 5px;-fx-border-radius: 0 10 10 10;-fx-background-radius: 0 10 10 10;");
			resultLabel.setStyle("-fx-font-size: 20px");
			rolled.setStyle("-fx-font-size: 16px");
		}

		innerMessage.getChildren().addAll(rolled, resultLabel);

		Label username = new Label(userName);
		username.setWrapText(true);
		username.maxWidthProperty().bind(customListCell.widthProperty().subtract(80));
		username.setStyle("-fx-text-fill: rgb(50, 50, 50)");
		root.getChildren().addAll(innerMessage, username);

		if(!own) {
			setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		}

		getChildren().addAll(triangle, root);
	}
}
