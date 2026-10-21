package com.shakeup.backend.domain;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "food_diary_entries")
public class FoodDiaryEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "eleve_id")
	private UserAccount eleve;

	@Column(name = "eaten_on", nullable = false)
	private LocalDate eatenOn;

	@Enumerated(EnumType.STRING)
	@Column(name = "meal_slot", nullable = false)
	private MealSlot mealSlot;

	@ManyToOne(optional = false)
	@JoinColumn(name = "food_id")
	private Food food;

	@Column(nullable = false)
	private double grams;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt = Instant.now();

	public Long getId() {
		return id;
	}

	public UserAccount getEleve() {
		return eleve;
	}

	public void setEleve(UserAccount eleve) {
		this.eleve = eleve;
	}

	public LocalDate getEatenOn() {
		return eatenOn;
	}

	public void setEatenOn(LocalDate eatenOn) {
		this.eatenOn = eatenOn;
	}

	public MealSlot getMealSlot() {
		return mealSlot;
	}

	public void setMealSlot(MealSlot mealSlot) {
		this.mealSlot = mealSlot;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public double getGrams() {
		return grams;
	}

	public void setGrams(double grams) {
		this.grams = grams;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
