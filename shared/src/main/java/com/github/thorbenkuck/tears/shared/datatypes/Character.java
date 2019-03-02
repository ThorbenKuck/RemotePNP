package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class Character implements Serializable {

	private final String characterName;
	private final List<Attribute> attributeList;
	private final List<BaseValue> baseValues;
	private NoteRepository repository;
	private int life;
	private int mentalHealth;
	private Notes notes;
	private static final long serialVersionUID = 3862873615528109717L;

	public Character(String characterName, List<Attribute> attributeList, List<BaseValue> baseValues, int life, int mentalHealth) {
		this(characterName, attributeList, baseValues, life, mentalHealth, new NoteRepository());
	}

	public Character(String characterName, List<Attribute> attributeList, List<BaseValue> baseValues, int life, int mentalHealth, NoteRepository repository) {
		this.characterName = characterName;
		this.attributeList = attributeList;
		this.baseValues = baseValues;
		this.life = life;
		this.mentalHealth = mentalHealth;
		this.repository = repository;
	}

	public void updateBy(Character character) {
		this.attributeList.clear();
		this.baseValues.clear();
		this.repository.clear();

		this.attributeList.addAll(character.getAttributeList());
		this.baseValues.addAll(character.getBaseValues());
		this.repository.addAll(character.repository);
		this.mentalHealth = character.mentalHealth;
		this.life = character.life;
	}

	public void setLife(int to) {
		this.life = to;
	}

	public void setMentalHealth(int to) {
		this.mentalHealth = to;
	}

	public String getCharacterName() {
		return characterName;
	}

	public List<Attribute> getAttributeList() {
		return attributeList;
	}

	public List<BaseValue> getBaseValues() {
		return baseValues;
	}

	public int getLife() {
		return life;
	}

	public int getMentalHealth() {
		return mentalHealth;
	}

	@Deprecated
	public Notes getNotes() {
		return notes;
	}

	public void shiftToNewFormat() {
		if(notes != null) {
			if(repository == null) {
				repository = new NoteRepository();
			}
			repository.add(new NotesInformation("Character Sheet", notes, new BitVector(false, true)));
			notes = null;
		}
	}

	@Override
	public String toString() {
		return characterName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Character character = (Character) o;
		return Objects.equals(characterName, character.characterName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(characterName);
	}

	public String detailedString() {
		return "Character{" +
				"name=" + characterName +
				", maxLife=" +life +
				", maxMental=" + mentalHealth +
				", attributes=" + attributeList +
				", baseValues=" + baseValues +
				", notes=" + repository +
				"}";
	}

	public NoteRepository getRepository() {
		return repository;
	}
}
