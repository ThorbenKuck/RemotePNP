package com;

import com.github.thorbenkuck.tears.shared.pipeline.Pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

public class PipelineTest {

	public static void main(String[] args) {
		Pipeline<String> pipeline = new Pipeline<>();

		pipeline.add((Consumer<String>) System.out::println)
				.add((Function<String, Integer>) Integer::parseInt)
				.add((Consumer<Integer>) i -> System.out.println("Integer: " + i))
				.add((Function<Integer, Integer>) i -> i *= 10)
				.add((Function<Integer, String>) i -> Integer.toString(i))
				.add((Consumer<String>) System.out::println);

		pipeline.run("123");
	}

}
