package com.shakeup.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.ExerciseKind;
import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.domain.WorkoutPlan;
import com.shakeup.backend.domain.WorkoutPlanItem;
import com.shakeup.backend.repository.ExerciseRepository;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.repository.WorkoutPlanRepository;
import com.shakeup.backend.web.dto.PlanRequest;
import com.shakeup.backend.web.dto.PlanResponse;

@Service
public class PlanService {

	private final UserAccountRepository users;
	private final WorkoutPlanRepository plans;
	private final ExerciseRepository exercises;

	public PlanService(
			UserAccountRepository users,
			WorkoutPlanRepository plans,
			ExerciseRepository exercises) {
		this.users = users;
		this.plans = plans;
		this.exercises = exercises;
	}

	@Transactional(readOnly = true)
	public PlanResponse getForEleve(Long eleveId) {
		return plans.findFirstByEleveAndActiveTrueOrderByIdDesc(requireEleve(eleveId))
				.map(PlanResponse::from)
				.orElse(null);
	}

	@Transactional(readOnly = true)
	public PlanResponse getMine(UserAccount eleve) {
		return plans.findFirstByEleveAndActiveTrueOrderByIdDesc(eleve)
				.map(PlanResponse::from)
				.orElse(null);
	}

	@Transactional
	public PlanResponse saveForEleve(Long eleveId, PlanRequest request) {
		UserAccount eleve = requireEleve(eleveId);
		plans.findFirstByEleveAndActiveTrueOrderByIdDesc(eleve).ifPresent(existing -> {
			existing.setActive(false);
			plans.save(existing);
		});

		WorkoutPlan plan = new WorkoutPlan();
		plan.setEleve(eleve);
		plan.setTitle(request.title().trim());
		plan.setNotes(request.notes());
		plan.setActive(true);

		int order = 0;
		for (var itemReq : request.items()) {
			var exercise = exercises.findById(itemReq.exerciseId())
					.filter(e -> !e.isHidden())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercice invalide"));
			WorkoutPlanItem item = new WorkoutPlanItem();
			item.setPlan(plan);
			item.setExercise(exercise);
			item.setSetsTarget(itemReq.setsTarget());
			item.setRestSeconds(itemReq.restSeconds());
			item.setSortOrder(order++);
			if (exercise.getKind() == ExerciseKind.HOLD) {
				if (itemReq.durationSeconds() == null || itemReq.durationSeconds() < 1) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Durée requise pour " + exercise.getName());
				}
				item.setDurationSeconds(itemReq.durationSeconds());
				item.setRepsTarget(null);
				item.setWeightKg(null);
			} else {
				if (itemReq.repsTarget() == null || itemReq.repsTarget() < 1) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Répétitions requises pour " + exercise.getName());
				}
				item.setRepsTarget(itemReq.repsTarget());
				item.setWeightKg(itemReq.weightKg());
				item.setDurationSeconds(null);
			}
			plan.getItems().add(item);
		}
		return PlanResponse.from(plans.save(plan));
	}

	private UserAccount requireEleve(Long id) {
		return users.findByIdAndRole(id, Role.ELEVE)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève introuvable"));
	}
}
