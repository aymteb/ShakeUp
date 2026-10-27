package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.CoachingWeekKind;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CoachingWeekRequest(
		@NotNull @Min(2000) @Max(2100) Integer isoYear,
		@NotNull @Min(1) @Max(53) Integer isoWeek,
		@NotNull CoachingWeekKind kind,
		@Size(max = 280) String note) {
}
