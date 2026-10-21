package com.shakeup.backend.web.dto;

import java.util.List;

import com.shakeup.backend.domain.WorkoutPlan;

public record PlanResponse(Long id, String title, String notes, List<PlanItemResponse> items) {

	public static PlanResponse from(WorkoutPlan plan) {
		return new PlanResponse(
				plan.getId(),
				plan.getTitle(),
				plan.getNotes(),
				plan.getItems().stream().map(PlanItemResponse::from).toList());
	}
}
