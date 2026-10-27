package com.shakeup.backend.web.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.shakeup.backend.domain.UserAccount;

public record EleveResponse(
		Long id,
		String email,
		String displayName,
		boolean mustChangePassword,
		Instant createdAt,
		LocalDate coachingStartsOn,
		LocalDate coachingEndsOn,
		boolean coachingActive) {

	public static EleveResponse from(UserAccount user, boolean coachingActive) {
		return new EleveResponse(
				user.getId(),
				user.getEmail(),
				user.getDisplayName(),
				user.isMustChangePassword(),
				user.getCreatedAt(),
				user.getCoachingStartsOn(),
				user.getCoachingEndsOn(),
				coachingActive);
	}
}
