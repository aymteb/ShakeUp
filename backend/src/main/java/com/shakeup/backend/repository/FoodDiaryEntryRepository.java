package com.shakeup.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.FoodDiaryEntry;
import com.shakeup.backend.domain.UserAccount;

public interface FoodDiaryEntryRepository extends JpaRepository<FoodDiaryEntry, Long> {

	List<FoodDiaryEntry> findByEleveAndEatenOnOrderByCreatedAtAsc(UserAccount eleve, LocalDate eatenOn);
}
