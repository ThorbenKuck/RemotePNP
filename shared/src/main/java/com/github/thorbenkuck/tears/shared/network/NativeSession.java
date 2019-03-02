package com.github.thorbenkuck.tears.shared.network;

import java.io.IOException;
import java.util.function.Function;

public class NativeSession implements Session {

	private final Connection connection;
	private final Function<Object, byte[]> convert;
	private boolean identified;
	private String identifier;

	public NativeSession(Connection connection, Function<Object, byte[]> convert) {
		this.connection = connection;
		this.convert = convert;
	}

	@Override
	public void send(Object o) {
		try {
			connection.write(convert.apply(o));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean isIdentified() {
		return identified;
	}

	@Override
	public void setIdentified(boolean identified) {
		this.identified = identified;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}