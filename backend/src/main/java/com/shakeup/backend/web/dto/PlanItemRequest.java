package com.shakeup.backend.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlanItemRequest(
		@NotNull Long exerciseId,
		@Min(1) int setsTarget,
		Integer repsTarget,
		Double weightKg,
		Integer durationSeconds,
		@Min(0) int restSeconds) {
}
