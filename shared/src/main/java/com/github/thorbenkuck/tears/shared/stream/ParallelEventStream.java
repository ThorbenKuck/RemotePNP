package com.github.thorbenkuck.tears.shared.stream;

import java.util.List;

public class ParallelEventStream<T> extends AbstractEventStream<T> {
	@Override
	protected void dispatch(List<ConcreteSubscription<T>> concreteSubscriptions, T t) {
		concreteSubscriptions.parallelStream()
				.forEach(sub -> sub.notify(t));
	}
}
