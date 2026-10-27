package com.shakeup.backend.domain;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "eleve_coaching_weeks",
		uniqueConstraints = @UniqueConstraint(columnNames = { "eleve_id", "iso_year", "iso_week" }))
public class EleveCoachingWeek {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "eleve_id")
	private UserAccount eleve;

	@Column(name = "iso_year", nullable = false)
	private int isoYear;

	@Column(name = "iso_week", nullable = false)
	private int isoWeek;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private CoachingWeekKind kind = CoachingWeekKind.FULL;

	@Column(length = 280)
	private String note;

	public Long getId() {
		return id;
	}

	public UserAccount getEleve() {
		return eleve;
	}

	public void setEleve(UserAccount eleve) {
		this.eleve = eleve;
	}

	public int getIsoYear() {
		return isoYear;
	}

	public void setIsoYear(int isoYear) {
		this.isoYear = isoYear;
	}

	public int getIsoWeek() {
		return isoWeek;
	}

	public void setIsoWeek(int isoWeek) {
		this.isoWeek = isoWeek;
	}

	public CoachingWeekKind getKind() {
		return kind;
	}

	public void setKind(CoachingWeekKind kind) {
		this.kind = kind;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
