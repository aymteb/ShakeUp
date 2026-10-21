package com.shakeup.backend.domain;

import java.time.Instant;

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
@Table(name = "feedback_messages")
public class FeedbackMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "eleve_id")
	private UserAccount eleve;

	@ManyToOne(optional = false)
	@JoinColumn(name = "author_id")
	private UserAccount author;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FeedbackChannel channel;

	@Column(nullable = false, length = 1000)
	private String body;

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

	public UserAccount getAuthor() {
		return author;
	}

	public void setAuthor(UserAccount author) {
		this.author = author;
	}

	public FeedbackChannel getChannel() {
		return channel;
	}

	public void setChannel(FeedbackChannel channel) {
		this.channel = channel;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
