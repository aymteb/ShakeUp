package com.shakeup.backend.web.dto;

import com.shakeup.backend.domain.FeedbackChannel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
		@NotNull FeedbackChannel channel,
		@NotBlank @Size(max = 1000) String body) {
}
