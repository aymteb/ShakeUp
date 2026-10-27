package com.shakeup.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.domain.WorkoutPlan;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

	@EntityGraph(attributePaths = { "items", "items.exercise" })
	Optional<WorkoutPlan> findFirstByEleveAndActiveTrueOrderByIdDesc(UserAccount eleve);
}
