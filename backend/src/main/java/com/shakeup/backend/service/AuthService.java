package com.shakeup.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.security.JwtService;
import com.shakeup.backend.web.dto.ChangePasswordRequest;
import com.shakeup.backend.web.dto.LoginRequest;
import com.shakeup.backend.web.dto.LoginResponse;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserAccountRepository users;
	private final PasswordEncoder passwordEncoder;

	public AuthService(
			AuthenticationManager authenticationManager,
			JwtService jwtService,
			UserAccountRepository users,
			PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.users = users;
		this.passwordEncoder = passwordEncoder;
	}

	public LoginResponse login(LoginRequest request) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		UserAccount user = (UserAccount) auth.getPrincipal();
		return new LoginResponse(
				jwtService.createToken(user),
				user.getRole().name(),
				user.getId(),
				user.getDisplayName(),
				user.isMustChangePassword());
	}

	public void changePassword(UserAccount user, ChangePasswordRequest request) {
		if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe actuel incorrect");
		}
		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		user.setMustChangePassword(false);
		users.save(user);
	}
}
