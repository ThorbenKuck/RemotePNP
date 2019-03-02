package com.github.thorbenkuck.tears.shared.messages.groups;

import java.io.Serializable;

public final class GMDistributeMessage implements Serializable {

	private static final long serialVersionUID = 2546706411470061147L;
	private final Serializable message;

	public GMDistributeMessage(Serializable message) {
		this.message = message;
	}

	public Serializable getMessage() {
		return message;
	}
}
