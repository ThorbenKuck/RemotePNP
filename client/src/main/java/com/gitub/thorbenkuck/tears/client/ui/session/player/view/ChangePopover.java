package com.gitub.thorbenkuck.tears.client.ui.session.player.view;

import com.gitub.thorbenkuck.tears.client.ui.TextFieldListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.util.function.Consumer;

public class ChangePopover {

	private final PopOver popOver = new PopOver();
	private final PopOver error = new PopOver(new Label("Dieses Feld muss ausgefüllt sein!"));
	private final Consumer<String> changedConsumer;

	public ChangePopover(Consumer<String> changedConsumer) {
		this.changedConsumer = changedConsumer;

		error.setDetachable(false);
		error.setAnimated(true);
		error.setOpacity(0.8);

		popOver.setDetachable(false);
		popOver.setAutoFix(false);
		popOver.setHideOnEscape(true);
		popOver.setCloseButtonEnabled(true);
	}

	public ChangePopover construct(String name, String initialValue) {
		TextField textField = new TextField(initialValue);
		TextFieldListener.integerOnly(textField);
		Button done = new Button("Fertig");
		Runnable r = () -> {
			if (textField.getText().isEmpty()) {
				if (error.isShowing()) {
					error.hide(Duration.millis(400));
				}
				error.show(textField);
			} else {
				changedConsumer.accept(textField.getText());
				popOver.hide(Duration.millis(300));
			}
		};
		done.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				e.consume();
				r.run();
			}
		});
		textField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				e.consume();
				r.run();
			} else if (e.getCode() == KeyCode.ESCAPE) {
				popOver.hide(Duration.millis(300));
			}
		});

		GridPane gridPane = new GridPane();
		gridPane.add(new Label(name), 0, 0);
		gridPane.add(textField, 1, 0);
		gridPane.add(done, 1, 1);

		popOver.setContentNode(gridPane);

		return this;
	}

	public ChangePopover setTitle(String title) {
		popOver.setTitle(title);

		return this;
	}

	public void show(Node node) {
		popOver.show(node);
	}

}
