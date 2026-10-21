package com.shakeup.backend.web.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanRequest(
		@NotBlank String title,
		String notes,
		@NotNull @Valid List<PlanItemRequest> items) {
}
