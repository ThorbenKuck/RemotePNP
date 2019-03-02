package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class TextFieldListener {

	public static void integerOnly(TextField textField) {
		textField.textProperty().addListener(new IntegerOnlyListener(textField));
	}

	public static void evaluate(TextField textField, String oldValue, String newValue) {
		if(newValue.isEmpty()) {
			return;
		}

		try {
			Integer.parseInt(newValue);
		} catch (NumberFormatException e) {
			textField.setText(oldValue);
			Logger.warn("Not a number!");
		}
	}

	private static class IntegerOnlyListener implements ChangeListener<String> {

		private final TextField textField;

		private IntegerOnlyListener(TextField textField) {
			this.textField = textField;
		}

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			if (!newValue.isEmpty()) {
				try {
					Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					textField.setText(oldValue);
					Logger.warn("Not a number!");
				}
			}
		}
	}
}
