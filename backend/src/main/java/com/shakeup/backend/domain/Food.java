package com.shakeup.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "foods")
public class Food {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "kcal_per_100g", nullable = false)
	private double kcalPer100g;

	@Column(name = "protein_per_100g", nullable = false)
	private double proteinPer100g;

	@Column(name = "carbs_per_100g", nullable = false)
	private double carbsPer100g;

	@Column(name = "fat_per_100g", nullable = false)
	private double fatPer100g;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "serving_grams")
	private Double servingGrams;

	@Column(name = "external_id")
	private String externalId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CatalogSource source;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getKcalPer100g() {
		return kcalPer100g;
	}

	public void setKcalPer100g(double kcalPer100g) {
		this.kcalPer100g = kcalPer100g;
	}

	public double getProteinPer100g() {
		return proteinPer100g;
	}

	public void setProteinPer100g(double proteinPer100g) {
		this.proteinPer100g = proteinPer100g;
	}

	public double getCarbsPer100g() {
		return carbsPer100g;
	}

	public void setCarbsPer100g(double carbsPer100g) {
		this.carbsPer100g = carbsPer100g;
	}

	public double getFatPer100g() {
		return fatPer100g;
	}

	public void setFatPer100g(double fatPer100g) {
		this.fatPer100g = fatPer100g;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Double getServingGrams() {
		return servingGrams;
	}

	public void setServingGrams(Double servingGrams) {
		this.servingGrams = servingGrams;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public CatalogSource getSource() {
		return source;
	}

	public void setSource(CatalogSource source) {
		this.source = source;
	}
}
