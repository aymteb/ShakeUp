package com.shakeup.backend.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.web.dto.CreateEleveRequest;
import com.shakeup.backend.web.dto.EleveResponse;
import com.shakeup.backend.web.dto.UpdateEleveRequest;

@Service
public class EleveService {

	private static final int DEFAULT_COACHING_WEEKS = 12;

	private final UserAccountRepository users;
	private final PasswordEncoder passwordEncoder;
	private final CoachingAccessService coachingAccess;
	private final Clock clock;

	public EleveService(
			UserAccountRepository users,
			PasswordEncoder passwordEncoder,
			CoachingAccessService coachingAccess,
			Clock clock) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.coachingAccess = coachingAccess;
		this.clock = clock;
	}

	public List<EleveResponse> list() {
		return users.findByRoleOrderByDisplayNameAsc(Role.ELEVE).stream()
				.map(this::toResponse)
				.toList();
	}

	public EleveResponse get(Long id) {
		return toResponse(requireEleve(id));
	}

	public EleveResponse create(CreateEleveRequest request) {
		if (users.existsByEmailIgnoreCase(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
		}
		LocalDate today = LocalDate.now(clock);
		LocalDate starts = request.coachingStartsOn() != null ? request.coachingStartsOn() : today;
		LocalDate ends = request.coachingEndsOn() != null
				? request.coachingEndsOn()
				: starts.plusWeeks(DEFAULT_COACHING_WEEKS);
		if (!ends.isAfter(starts)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fin doit être après le début");
		}
		UserAccount eleve = new UserAccount();
		eleve.setEmail(request.email().trim().toLowerCase());
		eleve.setDisplayName(request.displayName().trim());
		eleve.setPasswordHash(passwordEncoder.encode(request.temporaryPassword()));
		eleve.setRole(Role.ELEVE);
		eleve.setMustChangePassword(true);
		eleve.setCoachingStartsOn(starts);
		eleve.setCoachingEndsOn(ends);
		return toResponse(users.save(eleve));
	}

	public EleveResponse update(Long id, UpdateEleveRequest request) {
		UserAccount eleve = requireEleve(id);
		eleve.setDisplayName(request.displayName().trim());
		if (request.temporaryPassword() != null && !request.temporaryPassword().isBlank()) {
			eleve.setPasswordHash(passwordEncoder.encode(request.temporaryPassword()));
			eleve.setMustChangePassword(true);
		}
		if (request.coachingStartsOn() != null) {
			eleve.setCoachingStartsOn(request.coachingStartsOn());
		}
		if (request.coachingEndsOn() != null) {
			eleve.setCoachingEndsOn(request.coachingEndsOn());
		}
		LocalDate starts = eleve.getCoachingStartsOn();
		LocalDate ends = eleve.getCoachingEndsOn();
		if (starts != null && ends != null && !ends.isAfter(starts)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fin doit être après le début");
		}
		return toResponse(users.save(eleve));
	}

	public void delete(Long id) {
		users.delete(requireEleve(id));
	}

	private EleveResponse toResponse(UserAccount user) {
		return EleveResponse.from(user, coachingAccess.isCoachingActive(user));
	}

	private UserAccount requireEleve(Long id) {
		return users.findByIdAndRole(id, Role.ELEVE)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève introuvable"));
	}
}
