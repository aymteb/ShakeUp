package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.NutritionTarget;

public record NutritionTargetResponse(
		Long id,
		int caloriesKcal,
		Integer proteinG,
		Integer carbsG,
		Integer fatG,
		String coachNote) {

	public static NutritionTargetResponse from(NutritionTarget t) {
		return new NutritionTargetResponse(
				t.getId(),
				t.getCaloriesKcal(),
				t.getProteinG(),
				t.getCarbsG(),
				t.getFatG(),
				t.getCoachNote());
	}
}
