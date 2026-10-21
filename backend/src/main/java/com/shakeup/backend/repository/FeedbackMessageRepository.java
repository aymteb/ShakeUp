package com.shakeup.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.FeedbackChannel;
import com.shakeup.backend.domain.FeedbackMessage;
import com.shakeup.backend.domain.UserAccount;

public interface FeedbackMessageRepository extends JpaRepository<FeedbackMessage, Long> {

	List<FeedbackMessage> findByEleveAndChannelOrderByCreatedAtAsc(UserAccount eleve, FeedbackChannel channel);
}
