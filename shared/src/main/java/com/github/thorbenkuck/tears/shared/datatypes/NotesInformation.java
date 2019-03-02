package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;

public final class NotesInformation implements Serializable {

	private static final long serialVersionUID = 7784140492963172137L;
	private final Notes notes;
	private final BitVector bitVector;
	private String name;

	public NotesInformation(String name, Notes notes, BitVector bitVector) {
		this.notes = notes;
		this.bitVector = bitVector;
		this.name = name;
		if(bitVector.size() != 2) {
			throw new IllegalArgumentException("NotesInformation can only hold BitVectors of size 2");
		}
	}

	public NotesInformation(String name, Notes notes) {
		this(name, notes, new BitVector(2));
	}

	public NotesInformation(String name) {
		this(name, new Notes(), new BitVector(2));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Notes getNotes() {
		return notes;
	}

	public boolean publicVisible() {
		return bitVector.get(0);
	}

	public boolean visibleForGM() {
		return bitVector.get(1);
	}

	@Override
	public String toString() {
		if(publicVisible()) {
			return "(Public) " + name;
		} else if(visibleForGM()) {
			return "(DM) " + name;
		} else {
			return "(Private) " + name;
		}
	}

	public void updateBy(NotesInformation notesInformation) {
		notes.setContent(notesInformation.notes.getContent());
		bitVector.set(notesInformation.bitVector);
		name = notesInformation.name;
	}

	public void setPublicVisible(boolean publicAvailable) {
		bitVector.set(0, publicAvailable);
	}

	public void setGMVisible(boolean gmAvailable) {
		bitVector.set(1, gmAvailable);
	}
}
