package com.shakeup.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.FeedbackChannel;
import com.shakeup.backend.domain.FeedbackMessage;
import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.repository.FeedbackMessageRepository;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.web.dto.FeedbackRequest;
import com.shakeup.backend.web.dto.FeedbackResponse;

@Service
public class FeedbackService {

	private final UserAccountRepository users;
	private final FeedbackMessageRepository messages;

	public FeedbackService(UserAccountRepository users, FeedbackMessageRepository messages) {
		this.users = users;
		this.messages = messages;
	}

	public List<FeedbackResponse> list(Long eleveId, FeedbackChannel channel) {
		return messages.findByEleveAndChannelOrderByCreatedAtAsc(requireEleve(eleveId), channel).stream()
				.map(FeedbackResponse::from)
				.toList();
	}

	public List<FeedbackResponse> listMine(UserAccount eleve, FeedbackChannel channel) {
		return messages.findByEleveAndChannelOrderByCreatedAtAsc(eleve, channel).stream()
				.map(FeedbackResponse::from)
				.toList();
	}

	public FeedbackResponse postAsCoach(Long eleveId, UserAccount coach, FeedbackRequest request) {
		return save(requireEleve(eleveId), coach, request);
	}

	public FeedbackResponse postAsEleve(UserAccount eleve, FeedbackRequest request) {
		return save(eleve, eleve, request);
	}

	private FeedbackResponse save(UserAccount eleve, UserAccount author, FeedbackRequest request) {
		FeedbackMessage message = new FeedbackMessage();
		message.setEleve(eleve);
		message.setAuthor(author);
		message.setChannel(request.channel());
		message.setBody(request.body().trim());
		return FeedbackResponse.from(messages.save(message));
	}

	private UserAccount requireEleve(Long id) {
		return users.findByIdAndRole(id, Role.ELEVE)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève introuvable"));
	}
}
