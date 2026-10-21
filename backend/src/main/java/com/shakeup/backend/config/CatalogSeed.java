package com.shakeup.backend.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Exercise;
import com.shakeup.backend.domain.ExerciseKind;
import com.shakeup.backend.domain.Food;
import com.shakeup.backend.repository.ExerciseRepository;
import com.shakeup.backend.repository.FoodRepository;

@Component
@Order(2)
public class CatalogSeed implements ApplicationRunner {

	private final ExerciseRepository exercises;
	private final FoodRepository foods;

	public CatalogSeed(ExerciseRepository exercises, FoodRepository foods) {
		this.exercises = exercises;
		this.foods = foods;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (exercises.count() == 0) {
			saveExercise("Squat", "Jambes", ExerciseKind.LOAD);
			saveExercise("Développé couché", "Pectoraux", ExerciseKind.LOAD);
			saveExercise("Soulevé de terre", "Dos", ExerciseKind.LOAD);
			saveExercise("Rowing barre", "Dos", ExerciseKind.LOAD);
			saveExercise("Développé militaire", "Épaules", ExerciseKind.LOAD);
			saveExercise("Tractions", "Dos", ExerciseKind.LOAD);
			saveExercise("Curl biceps", "Bras", ExerciseKind.LOAD);
			saveExercise("Extension triceps", "Bras", ExerciseKind.LOAD);
			saveExercise("Fentes", "Jambes", ExerciseKind.LOAD);
			saveExercise("Gainage", "Core", ExerciseKind.HOLD);
		} else {
			for (Exercise e : exercises.findAll()) {
				if ("Gainage".equalsIgnoreCase(e.getName())) {
					if (e.getKind() != ExerciseKind.HOLD) {
						e.setKind(ExerciseKind.HOLD);
						exercises.save(e);
					}
				} else if (e.getKind() == null) {
					e.setKind(ExerciseKind.LOAD);
					exercises.save(e);
				}
			}
		}
		if (foods.count() == 0) {
			saveFood("Poulet (blanc)", 110, 23, 0, 1.5);
			saveFood("Riz blanc cuit", 130, 2.7, 28, 0.3);
			saveFood("Flocons d'avoine", 370, 13, 59, 7);
			saveFood("Oeuf entier", 140, 12.5, 0.7, 9.5);
			saveFood("Fromage blanc 0%", 50, 8, 4, 0.2);
			saveFood("Banane", 90, 1.1, 20, 0.3);
			saveFood("Saumon", 200, 20, 0, 13);
			saveFood("Brocoli", 35, 2.8, 4, 0.4);
			saveFood("Pain complet", 250, 9, 41, 4);
			saveFood("Huile d'olive", 900, 0, 0, 100);
			saveFood("Yaourt grec", 100, 9, 4, 5);
			saveFood("Patate douce", 90, 1.6, 20, 0.1);
		}
	}

	private void saveExercise(String name, String muscle, ExerciseKind kind) {
		Exercise e = new Exercise();
		e.setName(name);
		e.setMuscleGroup(muscle);
		e.setKind(kind);
		e.setSource(CatalogSource.SEED);
		e.setHidden(false);
		exercises.save(e);
	}

	private void saveFood(String name, double kcal, double p, double c, double f) {
		Food food = new Food();
		food.setName(name);
		food.setKcalPer100g(kcal);
		food.setProteinPer100g(p);
		food.setCarbsPer100g(c);
		food.setFatPer100g(f);
		food.setSource(CatalogSource.SEED);
		foods.save(food);
	}
}
