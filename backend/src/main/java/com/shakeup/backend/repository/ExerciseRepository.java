package com.shakeup.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

	List<Exercise> findByHiddenFalseOrderByNameAsc();

	List<Exercise> findAllByOrderByNameAsc();

	Optional<Exercise> findByNameIgnoreCase(String name);

	long count();
}
