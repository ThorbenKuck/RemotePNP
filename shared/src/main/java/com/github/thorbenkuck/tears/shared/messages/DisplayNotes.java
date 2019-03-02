package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.Notes;
import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class DisplayNotes implements Serializable {

	private static final long serialVersionUID = 8260406689681724883L;
	private final User origin;
	private final Notes notes;

	public DisplayNotes(User origin, Notes notes) {
		this.origin = origin;
		this.notes = notes;
	}

	public User getOrigin() {
		return origin;
	}

	public Notes getNotes() {
		return notes;
	}

	@Override
	public String toString() {
		return "DisplayNotes{" +
				"origin=" + origin +
				'}';
	}
}
