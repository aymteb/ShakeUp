package com.shakeup.backend.web.dto;

import java.time.LocalDate;
import java.util.List;

public record DiaryDayResponse(
		LocalDate date,
		List<DiaryEntryResponse> entries,
		double totalKcal,
		double totalProteinG,
		double totalCarbsG,
		double totalFatG) {
}
