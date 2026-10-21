package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.ExerciseKind;
import com.shakeup.backend.domain.WorkoutPlanItem;

public record PlanItemResponse(
		Long id,
		Long exerciseId,
		String exerciseName,
		String muscleGroup,
		ExerciseKind exerciseKind,
		int setsTarget,
		Integer repsTarget,
		Double weightKg,
		Integer durationSeconds,
		int restSeconds,
		int sortOrder) {

	public static PlanItemResponse from(WorkoutPlanItem item) {
		return new PlanItemResponse(
				item.getId(),
				item.getExercise().getId(),
				item.getExercise().getName(),
				item.getExercise().getMuscleGroup(),
				item.getExercise().getKind(),
				item.getSetsTarget(),
				item.getRepsTarget(),
				item.getWeightKg(),
				item.getDurationSeconds(),
				item.getRestSeconds(),
				item.getSortOrder());
	}
}
