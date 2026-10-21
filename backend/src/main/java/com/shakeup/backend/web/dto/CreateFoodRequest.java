package com.shakeup.backend.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFoodRequest(
		@NotBlank @Size(max = 120) String name,
		@Min(0) double kcalPer100g,
		@Min(0) double proteinPer100g,
		@Min(0) double carbsPer100g,
		@Min(0) double fatPer100g) {
}
