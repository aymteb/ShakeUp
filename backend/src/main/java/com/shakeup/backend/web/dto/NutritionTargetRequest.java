package com.shakeup.backend.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record NutritionTargetRequest(
		@Min(1) int caloriesKcal,
		Integer proteinG,
		Integer carbsG,
		Integer fatG,
		@Size(max = 500) String coachNote) {
}
