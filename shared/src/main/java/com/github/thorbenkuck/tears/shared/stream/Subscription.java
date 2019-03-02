package com.github.thorbenkuck.tears.shared.stream;

import java.util.List;

public interface Subscription {

	boolean isCanceled();

	void cancel();

	List<Throwable> getEncounteredErrors();

	boolean hasEncounteredErrors();

}
