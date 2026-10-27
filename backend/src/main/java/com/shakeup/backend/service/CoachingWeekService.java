package com.shakeup.backend.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.CoachingWeekKind;
import com.shakeup.backend.domain.EleveCoachingWeek;
import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.repository.EleveCoachingWeekRepository;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.web.dto.CoachingWeekRequest;
import com.shakeup.backend.web.dto.CoachingWeekResponse;

@Service
public class CoachingWeekService {

	private static final WeekFields ISO = WeekFields.ISO;

	private final EleveCoachingWeekRepository weeks;
	private final UserAccountRepository users;
	private final Clock clock;

	public CoachingWeekService(
			EleveCoachingWeekRepository weeks,
			UserAccountRepository users,
			Clock clock) {
		this.weeks = weeks;
		this.users = users;
		this.clock = clock;
	}

	public CoachingWeekResponse currentFor(UserAccount eleve) {
		LocalDate today = LocalDate.now(clock);
		return getOrDefault(eleve, today.get(ISO.weekBasedYear()), today.get(ISO.weekOfWeekBasedYear()));
	}

	public CoachingWeekResponse currentForEleve(Long eleveId) {
		return currentFor(requireEleve(eleveId));
	}

	public CoachingWeekResponse getOrDefault(UserAccount eleve, int isoYear, int isoWeek) {
		return weeks.findByEleveAndIsoYearAndIsoWeek(eleve, isoYear, isoWeek)
				.map(CoachingWeekResponse::from)
				.orElseGet(() -> CoachingWeekResponse.full(isoYear, isoWeek));
	}

	public List<CoachingWeekResponse> listForEleve(Long eleveId) {
		UserAccount eleve = requireEleve(eleveId);
		return weeks.findByEleveOrderByIsoYearDescIsoWeekDesc(eleve).stream()
				.map(CoachingWeekResponse::from)
				.toList();
	}

	public CoachingWeekResponse upsert(Long eleveId, CoachingWeekRequest request) {
		UserAccount eleve = requireEleve(eleveId);
		EleveCoachingWeek week = weeks
				.findByEleveAndIsoYearAndIsoWeek(eleve, request.isoYear(), request.isoWeek())
				.orElseGet(EleveCoachingWeek::new);
		week.setEleve(eleve);
		week.setIsoYear(request.isoYear());
		week.setIsoWeek(request.isoWeek());
		week.setKind(request.kind());
		week.setNote(blankToNull(request.note()));
		return CoachingWeekResponse.from(weeks.save(week));
	}

	public CoachingWeekKind kindOrFull(UserAccount eleve, int isoYear, int isoWeek) {
		return weeks.findByEleveAndIsoYearAndIsoWeek(eleve, isoYear, isoWeek)
				.map(EleveCoachingWeek::getKind)
				.orElse(CoachingWeekKind.FULL);
	}

	private UserAccount requireEleve(Long id) {
		return users.findByIdAndRole(id, Role.ELEVE)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève introuvable"));
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
