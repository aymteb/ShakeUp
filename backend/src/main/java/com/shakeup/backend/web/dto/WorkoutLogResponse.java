package com.shakeup.backend.web.dto;

import java.time.LocalDate;

import com.shakeup.backend.domain.WorkoutLog;

public record WorkoutLogResponse(Long id, LocalDate performedOn, boolean done, String comment, Long planId) {

	public static WorkoutLogResponse from(WorkoutLog log) {
		return new WorkoutLogResponse(
				log.getId(),
				log.getPerformedOn(),
				log.isDone(),
				log.getComment(),
				log.getPlan() != null ? log.getPlan().getId() : null);
	}
}
