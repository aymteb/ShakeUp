package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Food;
import com.shakeup.backend.service.FoodServingHints;

public record FoodResponse(
		Long id,
		String name,
		double kcalPer100g,
		double proteinPer100g,
		double carbsPer100g,
		double fatPer100g,
		String imageUrl,
		Double servingGrams,
		CatalogSource source,
		String externalId) {

	public static FoodResponse from(Food f) {
		Double serving = f.getServingGrams();
		if (serving == null || serving <= 0) {
			serving = FoodServingHints.estimateGrams(f.getName());
		}
		return new FoodResponse(
				f.getId(),
				f.getName(),
				f.getKcalPer100g(),
				f.getProteinPer100g(),
				f.getCarbsPer100g(),
				f.getFatPer100g(),
				f.getImageUrl(),
				serving,
				f.getSource(),
				f.getExternalId());
	}
}
