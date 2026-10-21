package com.shakeup.backend.web.dto;

import java.time.LocalDate;

import com.shakeup.backend.domain.FoodDiaryEntry;
import com.shakeup.backend.domain.MealSlot;

public record DiaryEntryResponse(
		Long id,
		LocalDate eatenOn,
		MealSlot mealSlot,
		Long foodId,
		String foodName,
		double grams,
		double kcal,
		double proteinG,
		double carbsG,
		double fatG) {

	public static DiaryEntryResponse from(FoodDiaryEntry e) {
		double factor = e.getGrams() / 100.0;
		return new DiaryEntryResponse(
				e.getId(),
				e.getEatenOn(),
				e.getMealSlot(),
				e.getFood().getId(),
				e.getFood().getName(),
				e.getGrams(),
				round(e.getFood().getKcalPer100g() * factor),
				round(e.getFood().getProteinPer100g() * factor),
				round(e.getFood().getCarbsPer100g() * factor),
				round(e.getFood().getFatPer100g() * factor));
	}

	private static double round(double v) {
		return Math.round(v * 10.0) / 10.0;
	}
}
