package com.shakeup.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.NutritionTarget;
import com.shakeup.backend.domain.UserAccount;

public interface NutritionTargetRepository extends JpaRepository<NutritionTarget, Long> {

	Optional<NutritionTarget> findFirstByEleveOrderByActiveFromDescIdDesc(UserAccount eleve);
}
