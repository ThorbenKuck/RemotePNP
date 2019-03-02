package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;

public final class BaseValue implements Serializable {

	private final String name;
	private int value;
	private static final long serialVersionUID = -819968541184712197L;

	public BaseValue(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + "(" + value + ")";
	}

	public void setValue(int i) {
		this.value = i;
	}
}
