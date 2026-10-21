package com.shakeup.backend.web.dto;

public record LoginResponse(
		String token,
		String role,
		Long userId,
		String displayName,
		boolean mustChangePassword) {
}
