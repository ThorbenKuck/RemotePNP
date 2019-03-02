package com.gitub.thorbenkuck.tears.client.ui;

public class Sound {

	private final String name;
	private final String path;

	public Sound(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "Sound{" +
				"name='" + name + '\'' +
				", path='" + path + '\'' +
				'}';
	}
}
