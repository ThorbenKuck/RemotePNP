package com.github.thorbenkuck.tears.shared.datatypes;

import java.io.Serializable;

public final class Notes implements Serializable {

	private String content;
	private static final long serialVersionUID = 4603461958642375838L;

	public Notes() {
		this("");
	}

	public Notes(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void append(String content) {
		this.content += content;
	}
}
