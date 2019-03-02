package com.github.thorbenkuck.tears.shared.stream;

public interface EventStream<T> {

	Subscription subscribe(Subscriber<T> subscriber);

}
