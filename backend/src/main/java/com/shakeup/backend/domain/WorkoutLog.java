package com.shakeup.backend.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "workout_logs", uniqueConstraints = @UniqueConstraint(columnNames = { "eleve_id", "performed_on" }))
public class WorkoutLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "eleve_id")
	private UserAccount eleve;

	@ManyToOne
	@JoinColumn(name = "plan_id")
	private WorkoutPlan plan;

	@Column(name = "performed_on", nullable = false)
	private LocalDate performedOn;

	@Column(nullable = false)
	private boolean done;

	@Column(length = 1000)
	private String comment;

	public Long getId() {
		return id;
	}

	public UserAccount getEleve() {
		return eleve;
	}

	public void setEleve(UserAccount eleve) {
		this.eleve = eleve;
	}

	public WorkoutPlan getPlan() {
		return plan;
	}

	public void setPlan(WorkoutPlan plan) {
		this.plan = plan;
	}

	public LocalDate getPerformedOn() {
		return performedOn;
	}

	public void setPerformedOn(LocalDate performedOn) {
		this.performedOn = performedOn;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
