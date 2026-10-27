package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.CoachingWeekKind;
import com.shakeup.backend.domain.EleveCoachingWeek;

public record CoachingWeekResponse(
		int isoYear,
		int isoWeek,
		CoachingWeekKind kind,
		String note) {

	public static CoachingWeekResponse from(EleveCoachingWeek week) {
		return new CoachingWeekResponse(
				week.getIsoYear(),
				week.getIsoWeek(),
				week.getKind(),
				week.getNote());
	}

	public static CoachingWeekResponse full(int isoYear, int isoWeek) {
		return new CoachingWeekResponse(isoYear, isoWeek, CoachingWeekKind.FULL, null);
	}
}
