package com.github.thorbenkuck;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTest {

	public static void main(String[] args) {
		Random random = ThreadLocalRandom.current();

		for(int i = 0 ; i < 100 ; i++) {
			System.out.println(random.nextInt(6) + 1);
		}
	}

}
