package com.shakeup.backend.service;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;

@Service
public class CoachingAccessService {

	private final Clock clock;

	public CoachingAccessService(Clock clock) {
		this.clock = clock;
	}

	public boolean isCoachingActive(UserAccount user) {
		if (user == null || user.getRole() != Role.ELEVE) {
			return true;
		}
		LocalDate endsOn = user.getCoachingEndsOn();
		if (endsOn == null) {
			return true;
		}
		LocalDate today = LocalDate.now(clock);
		return today.isBefore(endsOn);
	}
}
