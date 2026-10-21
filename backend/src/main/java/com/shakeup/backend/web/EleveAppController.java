package com.shakeup.backend.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shakeup.backend.domain.FeedbackChannel;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.service.CatalogService;
import com.shakeup.backend.service.FeedbackService;
import com.shakeup.backend.service.NutritionService;
import com.shakeup.backend.service.PlanService;
import com.shakeup.backend.service.WorkoutLogService;
import com.shakeup.backend.web.dto.DiaryDayResponse;
import com.shakeup.backend.web.dto.DiaryEntryRequest;
import com.shakeup.backend.web.dto.DiaryEntryResponse;
import com.shakeup.backend.web.dto.ExerciseResponse;
import com.shakeup.backend.web.dto.FeedbackRequest;
import com.shakeup.backend.web.dto.FeedbackResponse;
import com.shakeup.backend.web.dto.FoodResponse;
import com.shakeup.backend.web.dto.NutritionTargetResponse;
import com.shakeup.backend.web.dto.PlanResponse;
import com.shakeup.backend.web.dto.WorkoutLogRequest;
import com.shakeup.backend.web.dto.WorkoutLogResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/eleve")
@PreAuthorize("hasRole('ELEVE')")
public class EleveAppController {

	private final PlanService planService;
	private final WorkoutLogService workoutLogService;
	private final NutritionService nutritionService;
	private final CatalogService catalogService;
	private final FeedbackService feedbackService;

	public EleveAppController(
			PlanService planService,
			WorkoutLogService workoutLogService,
			NutritionService nutritionService,
			CatalogService catalogService,
			FeedbackService feedbackService) {
		this.planService = planService;
		this.workoutLogService = workoutLogService;
		this.nutritionService = nutritionService;
		this.catalogService = catalogService;
		this.feedbackService = feedbackService;
	}

	@GetMapping("/plan")
	public ResponseEntity<PlanResponse> plan(@AuthenticationPrincipal UserAccount eleve) {
		PlanResponse plan = planService.getMine(eleve);
		return plan == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(plan);
	}

	@PostMapping("/workout-logs")
	public WorkoutLogResponse log(
			@AuthenticationPrincipal UserAccount eleve,
			@Valid @RequestBody WorkoutLogRequest request) {
		return workoutLogService.upsertMine(eleve, request);
	}

	@GetMapping("/nutrition-target")
	public ResponseEntity<NutritionTargetResponse> target(@AuthenticationPrincipal UserAccount eleve) {
		NutritionTargetResponse target = nutritionService.getMyTarget(eleve);
		return target == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(target);
	}

	@GetMapping("/foods")
	public List<FoodResponse> foods() {
		return catalogService.listFoods();
	}

	@GetMapping("/exercises")
	public List<ExerciseResponse> exercises() {
		return catalogService.listExercises(false);
	}

	@GetMapping("/diary")
	public DiaryDayResponse diary(
			@AuthenticationPrincipal UserAccount eleve,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return nutritionService.getMyDiary(eleve, date);
	}

	@PostMapping("/diary")
	public DiaryEntryResponse addDiary(
			@AuthenticationPrincipal UserAccount eleve,
			@Valid @RequestBody DiaryEntryRequest request) {
		return nutritionService.addEntry(eleve, request);
	}

	@DeleteMapping("/diary/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDiary(@AuthenticationPrincipal UserAccount eleve, @PathVariable Long id) {
		nutritionService.deleteEntry(eleve, id);
	}

	@GetMapping("/feedback")
	public List<FeedbackResponse> feedback(
			@AuthenticationPrincipal UserAccount eleve,
			@RequestParam FeedbackChannel channel) {
		return feedbackService.listMine(eleve, channel);
	}

	@PostMapping("/feedback")
	public FeedbackResponse postFeedback(
			@AuthenticationPrincipal UserAccount eleve,
			@Valid @RequestBody FeedbackRequest request) {
		return feedbackService.postAsEleve(eleve, request);
	}
}
