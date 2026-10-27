package com.shakeup.backend.web.dto;

import java.time.LocalDate;

public record LoginResponse(
		String token,
		String role,
		Long userId,
		String displayName,
		boolean mustChangePassword,
		boolean coachingActive,
		LocalDate coachingStartsOn,
		LocalDate coachingEndsOn) {
}
