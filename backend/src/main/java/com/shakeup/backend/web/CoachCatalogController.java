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
import com.shakeup.backend.service.OffSyncService;
import com.shakeup.backend.service.WgerSyncService;
import com.shakeup.backend.web.dto.CreateExerciseRequest;
import com.shakeup.backend.web.dto.CreateFoodRequest;
import com.shakeup.backend.web.dto.ExerciseResponse;
import com.shakeup.backend.web.dto.FoodResponse;
import com.shakeup.backend.web.dto.OffSyncResult;
import com.shakeup.backend.web.dto.WgerSyncResult;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/coach")
@PreAuthorize("hasRole('COACH')")
public class CoachCatalogController {

	private final CatalogService catalogService;
	private final WgerSyncService wgerSyncService;
	private final OffSyncService offSyncService;

	public CoachCatalogController(
			CatalogService catalogService,
			WgerSyncService wgerSyncService,
			OffSyncService offSyncService) {
		this.catalogService = catalogService;
		this.wgerSyncService = wgerSyncService;
		this.offSyncService = offSyncService;
	}

	@GetMapping("/exercises")
	public List<ExerciseResponse> exercises() {
		return catalogService.listExercises(true);
	}

	@PostMapping("/exercises")
	public ExerciseResponse createExercise(@Valid @RequestBody CreateExerciseRequest request) {
		return catalogService.createExercise(request);
	}

	@PostMapping("/exercises/sync-wger")
	public WgerSyncResult syncWger() {
		return wgerSyncService.syncAll();
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

	@PostMapping("/foods/sync-off")
	public OffSyncResult syncOff() {
		return offSyncService.syncSeedQueries();
	}
}
