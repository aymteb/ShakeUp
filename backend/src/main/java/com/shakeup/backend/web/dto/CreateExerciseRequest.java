package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.ExerciseKind;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateExerciseRequest(
		@NotBlank @Size(max = 120) String name,
		@NotBlank @Size(max = 80) String muscleGroup,
		@NotNull ExerciseKind kind,
		String demoUrl) {
}
