package com.shakeup.backend.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.service.AuthService;
import com.shakeup.backend.web.dto.ChangePasswordRequest;
import com.shakeup.backend.web.dto.LoginRequest;
import com.shakeup.backend.web.dto.LoginResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/change-password")
	public void changePassword(
			@AuthenticationPrincipal UserAccount user,
			@Valid @RequestBody ChangePasswordRequest request) {
		authService.changePassword(user, request);
	}
}
