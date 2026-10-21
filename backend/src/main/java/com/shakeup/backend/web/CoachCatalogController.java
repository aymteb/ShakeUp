package com.shakeup.backend.web;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shakeup.backend.service.CatalogService;
import com.shakeup.backend.web.dto.CreateExerciseRequest;
import com.shakeup.backend.web.dto.CreateFoodRequest;
import com.shakeup.backend.web.dto.ExerciseResponse;
import com.shakeup.backend.web.dto.FoodResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/coach")
@PreAuthorize("hasRole('COACH')")
public class CoachCatalogController {

	private final CatalogService catalogService;

	public CoachCatalogController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@GetMapping("/exercises")
	public List<ExerciseResponse> exercises() {
		return catalogService.listExercises(true);
	}

	@PostMapping("/exercises")
	public ExerciseResponse createExercise(@Valid @RequestBody CreateExerciseRequest request) {
		return catalogService.createExercise(request);
	}

	@PatchMapping("/exercises/{id}/hidden")
	public ExerciseResponse hideExercise(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
		return catalogService.setExerciseHidden(id, Boolean.TRUE.equals(body.get("hidden")));
	}

	@GetMapping("/foods")
	public List<FoodResponse> foods() {
		return catalogService.listFoods();
	}

	@PostMapping("/foods")
	public FoodResponse createFood(@Valid @RequestBody CreateFoodRequest request) {
		return catalogService.createFood(request);
	}
}
