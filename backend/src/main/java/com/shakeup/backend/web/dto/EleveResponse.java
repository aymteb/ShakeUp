package com.shakeup.backend.web.dto;

import java.time.Instant;

import com.shakeup.backend.domain.UserAccount;

public record EleveResponse(
		Long id,
		String email,
		String displayName,
		boolean mustChangePassword,
		Instant createdAt) {

	public static EleveResponse from(UserAccount user) {
		return new EleveResponse(
				user.getId(),
				user.getEmail(),
				user.getDisplayName(),
				user.isMustChangePassword(),
				user.getCreatedAt());
	}
}
