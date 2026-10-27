package com.shakeup.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {

	List<Food> findAllByOrderByNameAsc();

	List<Food> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

	Optional<Food> findBySourceAndExternalId(CatalogSource source, String externalId);

	long countBySource(CatalogSource source);
}
