package com.github.thorbenkuck.tears.shared.stream;

public interface ConcreteSubscription<T> extends Subscription {

	void notify(T t);

}
