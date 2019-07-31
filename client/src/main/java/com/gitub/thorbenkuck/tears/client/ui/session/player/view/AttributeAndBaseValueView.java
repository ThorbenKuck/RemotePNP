package com.gitub.thorbenkuck.tears.client.ui.session.player.view;

import com.github.thorbenkuck.tears.shared.datatypes.Attribute;
import com.github.thorbenkuck.tears.shared.datatypes.BaseValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AttributeAndBaseValueView extends SplitPane {

	private final ListView<BaseValue> baseValueListView = new ListView<>();
	private final ListView<Attribute> attributeListView = new ListView<>();

	public AttributeAndBaseValueView(EventHandler<MouseEvent> baseValueListHandler) {
		construct();
		setPrefWidth(200000);
		setPrefHeight(200000);
		baseValueListView.setOnMouseClicked(baseValueListHandler);
	}

	private static Node createSpacer() {
		HBox spacer = new HBox();
		spacer.setPrefWidth(0);
		spacer.setPrefHeight(0);
		HBox.setHgrow(spacer, Priority.ALWAYS);

		return spacer;
	}

	private void construct() {
		baseValueListView.setPrefWidth(200);
		baseValueListView.setPrefHeight(200000);
		attributeListView.setPrefWidth(200);
		attributeListView.setPrefHeight(200000);
		VBox right = constructRight();
		VBox left = constructLeft();

		getItems().addAll(left, right);
	}

	private VBox constructLeft() {
		HBox box = new HBox(createSpacer(), new Label("Attribute"), createSpacer());

		return new VBox(box, baseValueListView);
	}

	private VBox constructRight() {
		HBox box = new HBox(createSpacer(), new Label("Fertigkeiten"), createSpacer());

		return new VBox(box, attributeListView);
	}

	public ListView<BaseValue> getBaseValueListView() {
		return baseValueListView;
	}

	public ListView<Attribute> getAttributeListView() {
		return attributeListView;
	}
}
