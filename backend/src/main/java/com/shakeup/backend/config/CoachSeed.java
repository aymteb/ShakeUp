package com.shakeup.backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.repository.UserAccountRepository;

@Component
@Order(1)
public class CoachSeed implements ApplicationRunner {

	private final UserAccountRepository users;
	private final PasswordEncoder passwordEncoder;

	public CoachSeed(UserAccountRepository users, PasswordEncoder passwordEncoder) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (users.existsByRole(Role.COACH)) {
			return;
		}
		UserAccount coach = new UserAccount();
		coach.setEmail("coach@shakeup.local");
		coach.setPasswordHash(passwordEncoder.encode("coach123"));
		coach.setRole(Role.COACH);
		coach.setDisplayName("Coach");
		coach.setMustChangePassword(false);
		users.save(coach);
	}
}
