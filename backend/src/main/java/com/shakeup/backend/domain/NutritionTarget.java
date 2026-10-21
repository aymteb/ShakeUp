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

@Entity
@Table(name = "nutrition_targets")
public class NutritionTarget {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "eleve_id")
	private UserAccount eleve;

	@Column(name = "calories_kcal", nullable = false)
	private int caloriesKcal;

	@Column(name = "protein_g")
	private Integer proteinG;

	@Column(name = "carbs_g")
	private Integer carbsG;

	@Column(name = "fat_g")
	private Integer fatG;

	@Column(name = "coach_note", length = 500)
	private String coachNote;

	@Column(name = "active_from", nullable = false)
	private LocalDate activeFrom = LocalDate.now();

	public Long getId() {
		return id;
	}

	public UserAccount getEleve() {
		return eleve;
	}

	public void setEleve(UserAccount eleve) {
		this.eleve = eleve;
	}

	public int getCaloriesKcal() {
		return caloriesKcal;
	}

	public void setCaloriesKcal(int caloriesKcal) {
		this.caloriesKcal = caloriesKcal;
	}

	public Integer getProteinG() {
		return proteinG;
	}

	public void setProteinG(Integer proteinG) {
		this.proteinG = proteinG;
	}

	public Integer getCarbsG() {
		return carbsG;
	}

	public void setCarbsG(Integer carbsG) {
		this.carbsG = carbsG;
	}

	public Integer getFatG() {
		return fatG;
	}

	public void setFatG(Integer fatG) {
		this.fatG = fatG;
	}

	public String getCoachNote() {
		return coachNote;
	}

	public void setCoachNote(String coachNote) {
		this.coachNote = coachNote;
	}

	public LocalDate getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(LocalDate activeFrom) {
		this.activeFrom = activeFrom;
	}
}
