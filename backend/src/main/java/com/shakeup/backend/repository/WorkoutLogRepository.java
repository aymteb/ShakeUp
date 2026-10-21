package com.shakeup.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.domain.WorkoutLog;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long> {

	Optional<WorkoutLog> findByEleveAndPerformedOn(UserAccount eleve, LocalDate performedOn);

	List<WorkoutLog> findByEleveOrderByPerformedOnDesc(UserAccount eleve);
}
