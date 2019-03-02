package com.github.thorbenkuck.tears.shared.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

public interface DynamicPipelineConnection<T> {

	<S> DynamicPipelineConnection<S> add(Function<T, S> function);

	DynamicPipelineConnection<T> add(Consumer<T> consumer);

}
