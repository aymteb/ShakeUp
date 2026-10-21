package com.shakeup.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.Food;

public interface FoodRepository extends JpaRepository<Food, Long> {

	List<Food> findAllByOrderByNameAsc();

	long count();
}
