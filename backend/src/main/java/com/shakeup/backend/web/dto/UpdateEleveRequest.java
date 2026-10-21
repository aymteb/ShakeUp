package com.shakeup.backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateEleveRequest(
		@NotBlank @Size(max = 120) String displayName,
		@Size(min = 6, max = 100) String temporaryPassword) {
}
