package com.github.thorbenkuck.tears.shared.messages.groups;

import java.io.Serializable;

public final class GMPublishMessage implements Serializable {

	private static final long serialVersionUID = 8482255448843297572L;
	private final Serializable toPublish;

	public GMPublishMessage(Serializable toPublish) {
		this.toPublish = toPublish;
	}

	public Serializable getToPublish() {
		return toPublish;
	}
}
