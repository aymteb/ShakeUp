package com.shakeup.backend.web.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record WorkoutLogRequest(
		@NotNull LocalDate performedOn,
		boolean done,
		String comment) {
}
