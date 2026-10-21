package com.shakeup.backend.web.dto;

import java.time.Instant;

import com.shakeup.backend.domain.FeedbackChannel;
import com.shakeup.backend.domain.FeedbackMessage;

public record FeedbackResponse(
		Long id,
		FeedbackChannel channel,
		String body,
		String authorName,
		String authorRole,
		Instant createdAt) {

	public static FeedbackResponse from(FeedbackMessage m) {
		return new FeedbackResponse(
				m.getId(),
				m.getChannel(),
				m.getBody(),
				m.getAuthor().getDisplayName(),
				m.getAuthor().getRole().name(),
				m.getCreatedAt());
	}
}
