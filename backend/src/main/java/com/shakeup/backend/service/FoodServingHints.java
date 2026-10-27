package com.shakeup.backend.service;

import java.util.Locale;

public final class FoodServingHints {

	private FoodServingHints() {
	}

	public static Double estimateGrams(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		String n = name.toLowerCase(Locale.ROOT);
		if (contains(n, "banane", "banana")) {
			return 120.0;
		}
		if (contains(n, "pomme", "apple")) {
			return 150.0;
		}
		if (contains(n, "orange") && !contains(n, "jus")) {
			return 130.0;
		}
		if (contains(n, "poire", "pear")) {
			return 160.0;
		}
		if (contains(n, "kiwi")) {
			return 75.0;
		}
		if (contains(n, "œuf", "oeuf", "egg")) {
			return 60.0;
		}
		if (contains(n, "avocat", "avocado")) {
			return 140.0;
		}
		if (contains(n, "tomate", "tomato") && !contains(n, "sauce", "coulis", "concentré")) {
			return 120.0;
		}
		if (contains(n, "mandarine", "clémentine", "clementine")) {
			return 80.0;
		}
		return null;
	}

	private static boolean contains(String haystack, String... needles) {
		for (String needle : needles) {
			if (haystack.contains(needle)) {
				return true;
			}
		}
		return false;
	}
}
