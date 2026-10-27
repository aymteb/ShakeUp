package com.shakeup.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.EleveCoachingWeek;
import com.shakeup.backend.domain.UserAccount;

public interface EleveCoachingWeekRepository extends JpaRepository<EleveCoachingWeek, Long> {

	Optional<EleveCoachingWeek> findByEleveAndIsoYearAndIsoWeek(
			UserAccount eleve, int isoYear, int isoWeek);

	List<EleveCoachingWeek> findByEleveOrderByIsoYearDescIsoWeekDesc(UserAccount eleve);
}
