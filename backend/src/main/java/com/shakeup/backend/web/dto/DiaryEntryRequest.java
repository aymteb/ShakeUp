package com.shakeup.backend.web.dto;

import java.time.LocalDate;

import com.shakeup.backend.domain.MealSlot;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DiaryEntryRequest(
		@NotNull LocalDate eatenOn,
		@NotNull MealSlot mealSlot,
		@NotNull Long foodId,
		@Min(1) double grams) {
}
