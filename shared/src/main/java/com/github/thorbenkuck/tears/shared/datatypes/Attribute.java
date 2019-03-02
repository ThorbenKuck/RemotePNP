package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;

public final class Attribute implements Serializable {

	private final String name;
	private int value;
	private static final long serialVersionUID = 7603802891026852122L;

	public Attribute(String name, int value) {
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
		value = i;
	}
}
