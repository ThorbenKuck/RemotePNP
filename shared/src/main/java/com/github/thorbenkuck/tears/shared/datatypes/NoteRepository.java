package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class NoteRepository implements Serializable {

	private static final long serialVersionUID = 8700728592185047464L;
	private final List<NotesInformation> informationList = new ArrayList<>();

	public void clear() {
		informationList.clear();
	}

	public void addAll(NoteRepository noteRepository) {
		informationList.addAll(noteRepository.informationList);
	}

	public void add(NotesInformation notesInformation) {
		informationList.add(notesInformation);
	}

	public void add(String name) {
		add(new NotesInformation(name));
	}

	public List<NotesInformation> publicAvailable() {
		return informationList.stream()
				.filter(NotesInformation::publicVisible)
				.collect(Collectors.toList());
	}

	public List<NotesInformation> dmAvailable() {
		return informationList.stream()
				.filter(n -> n.publicVisible() || n.visibleForGM())
				.collect(Collectors.toList());
	}

	public List<NotesInformation> all() {
		return new ArrayList<>(informationList);
	}

	@Override
	public String toString() {
		return "NoteRepository{" +
				"informationList=" + informationList +
				'}';
	}

	public boolean isEmpty() {
		return informationList.isEmpty();
	}

	public void update(NotesInformation notesInformation) {
		informationList.stream()
				.filter(i -> i.getName().equals(notesInformation.getName()))
				.findFirst()
				.ifPresent(i -> i.updateBy(notesInformation));
	}
}
