package com.shakeup.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

	List<Exercise> findByHiddenFalseOrderByNameAsc();

	List<Exercise> findAllByOrderByNameAsc();

	Optional<Exercise> findByNameIgnoreCase(String name);

	Optional<Exercise> findBySourceAndExternalId(CatalogSource source, Long externalId);

	long countBySource(CatalogSource source);

	long count();
}
