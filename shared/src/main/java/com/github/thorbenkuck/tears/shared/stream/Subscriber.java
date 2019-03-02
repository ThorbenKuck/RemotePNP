package com.github.thorbenkuck.tears.shared.stream;

@FunctionalInterface
public interface Subscriber<T> {

	void accept(T t);

}
