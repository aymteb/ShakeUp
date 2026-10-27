package com.shakeup.backend.web.dto;

import java.time.LocalDate;

import com.shakeup.backend.domain.CoachingWeekKind;

public record EleveAccessStatusResponse(
		boolean coachingActive,
		LocalDate coachingStartsOn,
		LocalDate coachingEndsOn,
		String displayName,
		String coachEmail,
		String coachDisplayName,
		LocalDate serverDate,
		int isoYear,
		int isoWeek,
		CoachingWeekKind coachingWeekKind) {
}
