package com.shakeup.backend.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shakeup.backend.domain.Exercise;
import com.shakeup.backend.domain.ExerciseKind;
import com.shakeup.backend.domain.NutritionTarget;
import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.domain.WorkoutPlan;
import com.shakeup.backend.domain.WorkoutPlanItem;
import com.shakeup.backend.repository.ExerciseRepository;
import com.shakeup.backend.repository.NutritionTargetRepository;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.repository.WorkoutPlanRepository;

@Component
@Order(3)
public class DemoDataSeed implements ApplicationRunner {

	public static final String ELEVE_EMAIL = "eleve@shakeup.local";
	public static final String ELEVE_PASSWORD = "eleve123";

	private final UserAccountRepository users;
	private final PasswordEncoder passwordEncoder;
	private final ExerciseRepository exercises;
	private final WorkoutPlanRepository plans;
	private final NutritionTargetRepository targets;

	public DemoDataSeed(
			UserAccountRepository users,
			PasswordEncoder passwordEncoder,
			ExerciseRepository exercises,
			WorkoutPlanRepository plans,
			NutritionTargetRepository targets) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.exercises = exercises;
		this.plans = plans;
		this.targets = targets;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		UserAccount eleve = users.findByEmailIgnoreCase(ELEVE_EMAIL).orElseGet(this::createEleve);
		if (plans.findFirstByEleveAndActiveTrueOrderByIdDesc(eleve).isEmpty()) {
			seedPlan(eleve);
		}
		if (targets.findFirstByEleveOrderByActiveFromDescIdDesc(eleve).isEmpty()) {
			seedTarget(eleve);
		}
	}

	private UserAccount createEleve() {
		UserAccount eleve = new UserAccount();
		eleve.setEmail(ELEVE_EMAIL);
		eleve.setPasswordHash(passwordEncoder.encode(ELEVE_PASSWORD));
		eleve.setRole(Role.ELEVE);
		eleve.setDisplayName("Camille Demo");
		eleve.setMustChangePassword(false);
		return users.save(eleve);
	}

	private void seedPlan(UserAccount eleve) {
		List<Exercise> catalog = exercises.findAll();
		Exercise squat = find(catalog, "Squat");
		Exercise bench = find(catalog, "Développé couché");
		Exercise plank = find(catalog, "Gainage");
		if (squat == null || bench == null || plank == null) {
			return;
		}

		WorkoutPlan plan = new WorkoutPlan();
		plan.setEleve(eleve);
		plan.setTitle("Séance A — full body");
		plan.setNotes("Démo seed : charges indicatives, à ajuster avec le coach.");
		plan.setActive(true);

		plan.getItems().add(loadItem(plan, squat, 0, 4, 8, 60.0, 90));
		plan.getItems().add(loadItem(plan, bench, 1, 4, 8, 40.0, 90));
		plan.getItems().add(holdItem(plan, plank, 2, 3, 45, 60));

		plans.save(plan);
	}

	private void seedTarget(UserAccount eleve) {
		NutritionTarget target = new NutritionTarget();
		target.setEleve(eleve);
		target.setCaloriesKcal(2450);
		target.setProteinG(160);
		target.setCarbsG(250);
		target.setFatG(70);
		target.setCoachNote("Vise les protéines à chaque repas. Jour d’entraînement : garde les glucides.");
		targets.save(target);
	}

	private static Exercise find(List<Exercise> catalog, String name) {
		return catalog.stream()
				.filter(e -> name.equalsIgnoreCase(e.getName()))
				.findFirst()
				.orElse(null);
	}

	private static WorkoutPlanItem loadItem(
			WorkoutPlan plan,
			Exercise exercise,
			int order,
			int sets,
			int reps,
			double weightKg,
			int rest) {
		WorkoutPlanItem item = new WorkoutPlanItem();
		item.setPlan(plan);
		item.setExercise(exercise);
		item.setSortOrder(order);
		item.setSetsTarget(sets);
		item.setRepsTarget(reps);
		item.setWeightKg(weightKg);
		item.setRestSeconds(rest);
		return item;
	}

	private static WorkoutPlanItem holdItem(
			WorkoutPlan plan,
			Exercise exercise,
			int order,
			int sets,
			int durationSeconds,
			int rest) {
		WorkoutPlanItem item = new WorkoutPlanItem();
		item.setPlan(plan);
		item.setExercise(exercise);
		item.setSortOrder(order);
		item.setSetsTarget(sets);
		item.setDurationSeconds(durationSeconds);
		item.setRestSeconds(rest);
		if (exercise.getKind() != ExerciseKind.HOLD) {
			exercise.setKind(ExerciseKind.HOLD);
		}
		return item;
	}
}
