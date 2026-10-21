package com.shakeup.backend.service;

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

	private final UserAccountRepository users;
	private final PasswordEncoder passwordEncoder;

	public EleveService(UserAccountRepository users, PasswordEncoder passwordEncoder) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
	}

	public List<EleveResponse> list() {
		return users.findByRoleOrderByDisplayNameAsc(Role.ELEVE).stream()
				.map(EleveResponse::from)
				.toList();
	}

	public EleveResponse get(Long id) {
		return EleveResponse.from(requireEleve(id));
	}

	public EleveResponse create(CreateEleveRequest request) {
		if (users.existsByEmailIgnoreCase(request.email())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
		}
		UserAccount eleve = new UserAccount();
		eleve.setEmail(request.email().trim().toLowerCase());
		eleve.setDisplayName(request.displayName().trim());
		eleve.setPasswordHash(passwordEncoder.encode(request.temporaryPassword()));
		eleve.setRole(Role.ELEVE);
		eleve.setMustChangePassword(true);
		return EleveResponse.from(users.save(eleve));
	}

	public EleveResponse update(Long id, UpdateEleveRequest request) {
		UserAccount eleve = requireEleve(id);
		eleve.setDisplayName(request.displayName().trim());
		if (request.temporaryPassword() != null && !request.temporaryPassword().isBlank()) {
			eleve.setPasswordHash(passwordEncoder.encode(request.temporaryPassword()));
			eleve.setMustChangePassword(true);
		}
		return EleveResponse.from(users.save(eleve));
	}

	public void delete(Long id) {
		users.delete(requireEleve(id));
	}

	private UserAccount requireEleve(Long id) {
		return users.findByIdAndRole(id, Role.ELEVE)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève introuvable"));
	}
}
