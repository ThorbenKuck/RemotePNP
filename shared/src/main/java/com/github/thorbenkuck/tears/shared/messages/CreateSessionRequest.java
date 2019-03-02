package com.github.thorbenkuck.tears.shared.messages;

import java.io.Serializable;

public final class CreateSessionRequest implements Serializable {

	private static final long serialVersionUID = 2269578819999075474L;
	private final String name;

	public CreateSessionRequest(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "CreateSessionRequest{" +
				"name='" + name + '\'' +
				'}';
	}
}
