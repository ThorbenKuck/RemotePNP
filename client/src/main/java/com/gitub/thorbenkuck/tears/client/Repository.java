package com.gitub.thorbenkuck.tears.client;

import com.github.thorbenkuck.tears.shared.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;

public class Repository extends Observable {

	private final Map<Class<?>, Object> mapping = new HashMap<>();

	public <T> void add(T object) {
		add((Class<T>) object.getClass(), object);
	}

	public <T> void add(Class<T> type, T object) {
		Logger.debug("Storing " + type + " with instance " + object);
		synchronized (mapping) {
			mapping.put(type, object);
		}
		setChanged();
		notifyObservers(object);
	}

	public <T> T get(Class<T> type) {
		T t;
		synchronized (mapping) {
			t = (T) mapping.get(type);
		}

		return Objects.requireNonNull(t, "Could not find any instance for " + type.getSimpleName());
	}

}
