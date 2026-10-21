package com.shakeup.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "workout_plan_items")
public class WorkoutPlanItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "plan_id")
	private WorkoutPlan plan;

	@ManyToOne(optional = false)
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;

	@Column(name = "sets_target", nullable = false)
	private int setsTarget;

	@Column(name = "reps_target")
	private Integer repsTarget;

	@Column(name = "weight_kg")
	private Double weightKg;

	@Column(name = "duration_seconds")
	private Integer durationSeconds;

	@Column(name = "rest_seconds", nullable = false)
	private int restSeconds;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	public Long getId() {
		return id;
	}

	public WorkoutPlan getPlan() {
		return plan;
	}

	public void setPlan(WorkoutPlan plan) {
		this.plan = plan;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	public int getSetsTarget() {
		return setsTarget;
	}

	public void setSetsTarget(int setsTarget) {
		this.setsTarget = setsTarget;
	}

	public Integer getRepsTarget() {
		return repsTarget;
	}

	public void setRepsTarget(Integer repsTarget) {
		this.repsTarget = repsTarget;
	}

	public Double getWeightKg() {
		return weightKg;
	}

	public void setWeightKg(Double weightKg) {
		this.weightKg = weightKg;
	}

	public Integer getDurationSeconds() {
		return durationSeconds;
	}

	public void setDurationSeconds(Integer durationSeconds) {
		this.durationSeconds = durationSeconds;
	}

	public int getRestSeconds() {
		return restSeconds;
	}

	public void setRestSeconds(int restSeconds) {
		this.restSeconds = restSeconds;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
}
