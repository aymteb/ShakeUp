package com.shakeup.backend.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "eleve_id")
	private UserAccount eleve;

	@Column(nullable = false)
	private String title;

	@Column(length = 1000)
	private String notes;

	@Column(nullable = false)
	private boolean active = true;

	@OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC")
	private List<WorkoutPlanItem> items = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public UserAccount getEleve() {
		return eleve;
	}

	public void setEleve(UserAccount eleve) {
		this.eleve = eleve;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<WorkoutPlanItem> getItems() {
		return items;
	}
}
