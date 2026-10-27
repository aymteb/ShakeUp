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
@Table(name = "exercises")
public class Exercise {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "muscle_group", nullable = false)
	private String muscleGroup;

	@Column(name = "demo_url")
	private String demoUrl;

	@Column(name = "external_id")
	private Long externalId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CatalogSource source;

	@Enumerated(EnumType.STRING)
	private ExerciseKind kind;

	@Column(nullable = false)
	private boolean hidden;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMuscleGroup() {
		return muscleGroup;
	}

	public void setMuscleGroup(String muscleGroup) {
		this.muscleGroup = muscleGroup;
	}

	public String getDemoUrl() {
		return demoUrl;
	}

	public void setDemoUrl(String demoUrl) {
		this.demoUrl = demoUrl;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public CatalogSource getSource() {
		return source;
	}

	public void setSource(CatalogSource source) {
		this.source = source;
	}

	public ExerciseKind getKind() {
		return kind != null ? kind : ExerciseKind.LOAD;
	}

	public void setKind(ExerciseKind kind) {
		this.kind = kind;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}
