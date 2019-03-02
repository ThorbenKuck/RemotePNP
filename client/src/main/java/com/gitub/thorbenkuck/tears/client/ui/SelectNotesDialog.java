package com.gitub.thorbenkuck.tears.client.ui;

import com.github.thorbenkuck.tears.shared.datatypes.NotesInformation;
import javafx.scene.control.Dialog;

public class SelectNotesDialog extends Dialog<NotesInformation> {

	public SelectNotesDialog() {
		setHeaderText("Notizen Auswahl");
		setContentText("Welche Notizen sollen gezeigt werden?");
	}

}
