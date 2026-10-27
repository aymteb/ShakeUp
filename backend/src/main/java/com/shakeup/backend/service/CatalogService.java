package com.shakeup.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Exercise;
import com.shakeup.backend.domain.ExerciseKind;
import com.shakeup.backend.domain.Food;
import com.shakeup.backend.repository.ExerciseRepository;
import com.shakeup.backend.repository.FoodRepository;
import com.shakeup.backend.web.dto.CreateExerciseRequest;
import com.shakeup.backend.web.dto.CreateFoodRequest;
import com.shakeup.backend.web.dto.ExerciseResponse;
import com.shakeup.backend.web.dto.FoodResponse;

@Service
public class CatalogService {

	private final ExerciseRepository exercises;
	private final FoodRepository foods;

	public CatalogService(ExerciseRepository exercises, FoodRepository foods) {
		this.exercises = exercises;
		this.foods = foods;
	}

	public List<ExerciseResponse> listExercises(boolean includeHidden) {
		var list = includeHidden ? exercises.findAllByOrderByNameAsc() : exercises.findByHiddenFalseOrderByNameAsc();
		return list.stream().map(ExerciseResponse::from).toList();
	}

	public ExerciseResponse createExercise(CreateExerciseRequest request) {
		Exercise e = new Exercise();
		e.setName(request.name().trim());
		e.setMuscleGroup(request.muscleGroup().trim());
		e.setKind(request.kind() != null ? request.kind() : ExerciseKind.LOAD);
		e.setDemoUrl(blankToNull(request.demoUrl()));
		e.setSource(CatalogSource.CUSTOM);
		e.setHidden(false);
		return ExerciseResponse.from(exercises.save(e));
	}

	public ExerciseResponse setExerciseHidden(Long id, boolean hidden) {
		Exercise e = exercises.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		e.setHidden(hidden);
		return ExerciseResponse.from(exercises.save(e));
	}

	public List<FoodResponse> listFoods() {
		return foods.findAllByOrderByNameAsc().stream().map(FoodResponse::from).toList();
	}

	public List<FoodResponse> searchLocalFoods(String query) {
		String q = query == null ? "" : query.trim();
		if (q.isEmpty()) {
			return listFoods();
		}
		return foods.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
				.map(FoodResponse::from)
				.toList();
	}

	public FoodResponse createFood(CreateFoodRequest request) {
		Food f = new Food();
		f.setName(request.name().trim());
		f.setKcalPer100g(request.kcalPer100g());
		f.setProteinPer100g(request.proteinPer100g());
		f.setCarbsPer100g(request.carbsPer100g());
		f.setFatPer100g(request.fatPer100g());
		f.setSource(CatalogSource.CUSTOM);
		return FoodResponse.from(foods.save(f));
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
