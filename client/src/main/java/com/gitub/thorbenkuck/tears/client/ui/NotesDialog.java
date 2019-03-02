package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.web.WebView;

public class NotesDialog extends Dialog<Void> {

	public NotesDialog(NotesInformation information) {
		setTitle("Notizen (" + information.getName() + ")");
		setResizable(true);

		WebView webView = Markdown.toWebView(information.getNotes());

		getDialogPane().setContent(webView);
		getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

		setResultConverter(buttonType -> null);

		setResizable(true);
	}

}
