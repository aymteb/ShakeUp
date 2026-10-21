package com.shakeup.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.domain.WorkoutLog;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.repository.WorkoutLogRepository;
import com.shakeup.backend.repository.WorkoutPlanRepository;
import com.shakeup.backend.web.dto.WorkoutLogRequest;
import com.shakeup.backend.web.dto.WorkoutLogResponse;

@Service
public class WorkoutLogService {

	private final UserAccountRepository users;
	private final WorkoutLogRepository logs;
	private final WorkoutPlanRepository plans;

	public WorkoutLogService(
			UserAccountRepository users,
			WorkoutLogRepository logs,
			WorkoutPlanRepository plans) {
		this.users = users;
		this.logs = logs;
		this.plans = plans;
	}

	public List<WorkoutLogResponse> listForEleve(Long eleveId) {
		return logs.findByEleveOrderByPerformedOnDesc(requireEleve(eleveId)).stream()
				.map(WorkoutLogResponse::from)
				.toList();
	}

	public WorkoutLogResponse upsertMine(UserAccount eleve, WorkoutLogRequest request) {
		WorkoutLog log = logs.findByEleveAndPerformedOn(eleve, request.performedOn())
				.orElseGet(WorkoutLog::new);
		log.setEleve(eleve);
		log.setPerformedOn(request.performedOn());
		log.setDone(request.done());
		log.setComment(request.comment());
		plans.findFirstByEleveAndActiveTrueOrderByIdDesc(eleve).ifPresent(log::setPlan);
		return WorkoutLogResponse.from(logs.save(log));
	}

	private UserAccount requireEleve(Long id) {
		return users.findByIdAndRole(id, Role.ELEVE)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève introuvable"));
	}
}
