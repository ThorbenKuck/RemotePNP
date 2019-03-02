package com.github.thorbenkuck.tears.shared.messages;

import java.io.Serializable;

public final class DownloadDone implements Serializable {
	private static final long serialVersionUID = 8056096612128289555L;

	private final String id;

	public DownloadDone(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
