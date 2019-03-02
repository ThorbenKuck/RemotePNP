package com.github.thorbenkuck.tears.shared.stream;

public interface WriteableEventStream<T> extends EventStream<T> {

	void clear();

	void push(T t);

}
