package com.shakeup.backend.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shakeup.backend.domain.FeedbackChannel;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.service.CoachingWeekService;
import com.shakeup.backend.service.FeedbackService;
import com.shakeup.backend.service.NutritionService;
import com.shakeup.backend.service.PlanService;
import com.shakeup.backend.service.WorkoutLogService;
import com.shakeup.backend.web.dto.CoachingWeekRequest;
import com.shakeup.backend.web.dto.CoachingWeekResponse;
import com.shakeup.backend.web.dto.DiaryDayResponse;
import com.shakeup.backend.web.dto.FeedbackRequest;
import com.shakeup.backend.web.dto.FeedbackResponse;
import com.shakeup.backend.web.dto.NutritionTargetRequest;
import com.shakeup.backend.web.dto.NutritionTargetResponse;
import com.shakeup.backend.web.dto.PlanRequest;
import com.shakeup.backend.web.dto.PlanResponse;
import com.shakeup.backend.web.dto.WorkoutLogResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/coach/eleves/{eleveId}")
@PreAuthorize("hasRole('COACH')")
public class CoachProgramController {

	private final PlanService planService;
	private final WorkoutLogService workoutLogService;
	private final NutritionService nutritionService;
	private final FeedbackService feedbackService;
	private final CoachingWeekService coachingWeeks;

	public CoachProgramController(
			PlanService planService,
			WorkoutLogService workoutLogService,
			NutritionService nutritionService,
			FeedbackService feedbackService,
			CoachingWeekService coachingWeeks) {
		this.planService = planService;
		this.workoutLogService = workoutLogService;
		this.nutritionService = nutritionService;
		this.feedbackService = feedbackService;
		this.coachingWeeks = coachingWeeks;
	}

	@GetMapping("/plan")
	public ResponseEntity<PlanResponse> getPlan(@PathVariable Long eleveId) {
		PlanResponse plan = planService.getForEleve(eleveId);
		return plan == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(plan);
	}

	@PutMapping("/plan")
	public PlanResponse savePlan(@PathVariable Long eleveId, @Valid @RequestBody PlanRequest request) {
		return planService.saveForEleve(eleveId, request);
	}

	@GetMapping("/workout-logs")
	public List<WorkoutLogResponse> logs(@PathVariable Long eleveId) {
		return workoutLogService.listForEleve(eleveId);
	}

	@GetMapping("/nutrition-target")
	public ResponseEntity<NutritionTargetResponse> getTarget(@PathVariable Long eleveId) {
		NutritionTargetResponse target = nutritionService.getTarget(eleveId);
		return target == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(target);
	}

	@PutMapping("/nutrition-target")
	public NutritionTargetResponse saveTarget(
			@PathVariable Long eleveId,
			@Valid @RequestBody NutritionTargetRequest request) {
		return nutritionService.saveTarget(eleveId, request);
	}

	@GetMapping("/diary")
	public DiaryDayResponse diary(
			@PathVariable Long eleveId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return nutritionService.getDiary(eleveId, date);
	}

	@GetMapping("/coaching-weeks")
	public List<CoachingWeekResponse> coachingWeeks(@PathVariable Long eleveId) {
		return coachingWeeks.listForEleve(eleveId);
	}

	@GetMapping("/coaching-weeks/current")
	public CoachingWeekResponse currentCoachingWeek(@PathVariable Long eleveId) {
		return coachingWeeks.currentForEleve(eleveId);
	}

	@PutMapping("/coaching-weeks")
	public CoachingWeekResponse saveCoachingWeek(
			@PathVariable Long eleveId,
			@Valid @RequestBody CoachingWeekRequest request) {
		return coachingWeeks.upsert(eleveId, request);
	}

	@GetMapping("/feedback")
	public List<FeedbackResponse> feedback(
			@PathVariable Long eleveId,
			@RequestParam FeedbackChannel channel) {
		return feedbackService.list(eleveId, channel);
	}

	@PostMapping("/feedback")
	public FeedbackResponse postFeedback(
			@PathVariable Long eleveId,
			@AuthenticationPrincipal UserAccount coach,
			@Valid @RequestBody FeedbackRequest request) {
		return feedbackService.postAsCoach(eleveId, coach, request);
	}
}
