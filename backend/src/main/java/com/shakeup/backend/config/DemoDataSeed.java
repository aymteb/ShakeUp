package com.shakeup.backend.config;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shakeup.backend.domain.CatalogSource;
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
@Order(4)
public class DemoDataSeed implements ApplicationRunner {

	public static final String ELEVE_EMAIL = "eleve@shakeup.local";
	public static final String ELEVE_PASSWORD = "eleve123";

	private final UserAccountRepository users;
	private final PasswordEncoder passwordEncoder;
	private final ExerciseRepository exercises;
	private final WorkoutPlanRepository plans;
	private final NutritionTargetRepository targets;
	private final Clock clock;

	public DemoDataSeed(
			UserAccountRepository users,
			PasswordEncoder passwordEncoder,
			ExerciseRepository exercises,
			WorkoutPlanRepository plans,
			NutritionTargetRepository targets,
			Clock clock) {
		this.users = users;
		this.passwordEncoder = passwordEncoder;
		this.exercises = exercises;
		this.plans = plans;
		this.targets = targets;
		this.clock = clock;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		UserAccount eleve = users.findByEmailIgnoreCase(ELEVE_EMAIL).orElseGet(this::createEleve);
		ensureCoachingWindow(eleve);
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
		applyDefaultWindow(eleve);
		return users.save(eleve);
	}

	private void ensureCoachingWindow(UserAccount eleve) {
		if (eleve.getCoachingStartsOn() != null && eleve.getCoachingEndsOn() != null) {
			return;
		}
		applyDefaultWindow(eleve);
		users.save(eleve);
	}

	private void applyDefaultWindow(UserAccount eleve) {
		LocalDate today = LocalDate.now(clock);
		eleve.setCoachingStartsOn(today.minusDays(30));
		eleve.setCoachingEndsOn(today.plusWeeks(12));
	}

	private void seedPlan(UserAccount eleve) {
		Exercise squat = ensureDemoExercise("Squat", "Jambes", ExerciseKind.LOAD);
		Exercise bench = ensureDemoExercise("Développé couché", "Pectoraux", ExerciseKind.LOAD);
		Exercise plank = ensureDemoExercise("Gainage", "Core", ExerciseKind.HOLD);

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

	private Exercise ensureDemoExercise(String name, String muscle, ExerciseKind kind) {
		return exercises.findByNameIgnoreCase(name).orElseGet(() -> {
			Exercise e = new Exercise();
			e.setName(name);
			e.setMuscleGroup(muscle);
			e.setKind(kind);
			e.setSource(CatalogSource.CUSTOM);
			e.setHidden(false);
			return exercises.save(e);
		});
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
