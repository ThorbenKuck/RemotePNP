package com.github.thorbenkuck.tears.shared.network;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CommunicationRegistration {

	private final Map<Class, BiConsumer> mapping = new HashMap<>();
	private BiConsumer<Connection, Object> defaultHandler = (c, o) -> {
		throw new IllegalStateException("Nothing registered for " + o);
	};

	public <T> void register(Class<T> type, BiConsumer<Connection, T> connectionSessionTTriConsumer) {
		mapping.put(type, connectionSessionTTriConsumer);
	}

	public void setDefaultHandler(BiConsumer<Connection, Object> defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public <T> void trigger(T t, Connection connection) {
		Logger.debug("Trying to handle " + t.getClass());
		BiConsumer<Connection, T> consumer = mapping.get(t.getClass());
		if (consumer != null) {
			consumer.accept(connection, t);
		} else {
			defaultHandler.accept(connection, t);
		}
	}
}
