package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.Food;

public record FoodResponse(
		Long id,
		String name,
		double kcalPer100g,
		double proteinPer100g,
		double carbsPer100g,
		double fatPer100g) {

	public static FoodResponse from(Food f) {
		return new FoodResponse(
				f.getId(),
				f.getName(),
				f.getKcalPer100g(),
				f.getProteinPer100g(),
				f.getCarbsPer100g(),
				f.getFatPer100g());
	}
}
