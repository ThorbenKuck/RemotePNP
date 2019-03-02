package com.github.thorbenkuck.tears.shared.messages;

import com.github.thorbenkuck.tears.shared.datatypes.User;

import java.io.Serializable;

public final class UploadRequest implements Serializable {

	private static final long serialVersionUID = 8727139310659636390L;
	private final User target;
	private final String id;
	private final String fileName;
	private final byte[] data;

	public UploadRequest(String id, String fileName, byte[] data) {
		this(null, id, fileName, data);
	}

	public UploadRequest(User target, String id, String fileName, byte[] data) {
		this.target = target;
		this.id = id;
		this.fileName = fileName;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public User getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return "UploadRequest{" +
				", target=" + target +
				", id='" + id + '\'' +
				'}';
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getData() {
		return data;
	}
}
