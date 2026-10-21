package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.Exercise;
import com.shakeup.backend.domain.ExerciseKind;

public record ExerciseResponse(
		Long id,
		String name,
		String muscleGroup,
		String demoUrl,
		ExerciseKind kind,
		boolean hidden) {

	public static ExerciseResponse from(Exercise e) {
		return new ExerciseResponse(
				e.getId(),
				e.getName(),
				e.getMuscleGroup(),
				e.getDemoUrl(),
				e.getKind(),
				e.isHidden());
	}
}
