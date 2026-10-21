package com.shakeup.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.shakeup.backend.domain.FoodDiaryEntry;
import com.shakeup.backend.domain.NutritionTarget;
import com.shakeup.backend.domain.Role;
import com.shakeup.backend.domain.UserAccount;
import com.shakeup.backend.repository.FoodDiaryEntryRepository;
import com.shakeup.backend.repository.FoodRepository;
import com.shakeup.backend.repository.NutritionTargetRepository;
import com.shakeup.backend.repository.UserAccountRepository;
import com.shakeup.backend.web.dto.DiaryDayResponse;
import com.shakeup.backend.web.dto.DiaryEntryRequest;
import com.shakeup.backend.web.dto.DiaryEntryResponse;
import com.shakeup.backend.web.dto.NutritionTargetRequest;
import com.shakeup.backend.web.dto.NutritionTargetResponse;

@Service
public class NutritionService {

	private final UserAccountRepository users;
	private final NutritionTargetRepository targets;
	private final FoodDiaryEntryRepository diary;
	private final FoodRepository foods;

	public NutritionService(
			UserAccountRepository users,
			NutritionTargetRepository targets,
			FoodDiaryEntryRepository diary,
			FoodRepository foods) {
		this.users = users;
		this.targets = targets;
		this.diary = diary;
		this.foods = foods;
	}

	public NutritionTargetResponse getTarget(Long eleveId) {
		return targets.findFirstByEleveOrderByActiveFromDescIdDesc(requireEleve(eleveId))
				.map(NutritionTargetResponse::from)
				.orElse(null);
	}

	public NutritionTargetResponse getMyTarget(UserAccount eleve) {
		return targets.findFirstByEleveOrderByActiveFromDescIdDesc(eleve)
				.map(NutritionTargetResponse::from)
				.orElse(null);
	}

	public NutritionTargetResponse saveTarget(Long eleveId, NutritionTargetRequest request) {
		UserAccount eleve = requireEleve(eleveId);
		NutritionTarget target = targets.findFirstByEleveOrderByActiveFromDescIdDesc(eleve)
				.orElseGet(NutritionTarget::new);
		target.setEleve(eleve);
		target.setCaloriesKcal(request.caloriesKcal());
		target.setProteinG(request.proteinG());
		target.setCarbsG(request.carbsG());
		target.setFatG(request.fatG());
		target.setCoachNote(request.coachNote());
		target.setActiveFrom(LocalDate.now());
		return NutritionTargetResponse.from(targets.save(target));
	}

	public DiaryDayResponse getDiary(Long eleveId, LocalDate date) {
		return buildDay(requireEleve(eleveId), date);
	}

	public DiaryDayResponse getMyDiary(UserAccount eleve, LocalDate date) {
		return buildDay(eleve, date);
	}

	public DiaryEntryResponse addEntry(UserAccount eleve, DiaryEntryRequest request) {
		var food = foods.findById(request.foodId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aliment invalide"));
		FoodDiaryEntry entry = new FoodDiaryEntry();
		entry.setEleve(eleve);
		entry.setEatenOn(request.eatenOn());
		entry.setMealSlot(request.mealSlot());
		entry.setFood(food);
		entry.setGrams(request.grams());
		return DiaryEntryResponse.from(diary.save(entry));
	}

	public void deleteEntry(UserAccount eleve, Long entryId) {
		FoodDiaryEntry entry = diary.findById(entryId)
				.filter(e -> e.getEleve().getId().equals(eleve.getId()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		diary.delete(entry);
	}

	private DiaryDayResponse buildDay(UserAccount eleve, LocalDate date) {
		List<DiaryEntryResponse> entries = diary.findByEleveAndEatenOnOrderByCreatedAtAsc(eleve, date).stream()
				.map(DiaryEntryResponse::from)
				.toList();
		double kcal = entries.stream().mapToDouble(DiaryEntryResponse::kcal).sum();
		double p = entries.stream().mapToDouble(DiaryEntryResponse::proteinG).sum();
		double c = entries.stream().mapToDouble(DiaryEntryResponse::carbsG).sum();
		double f = entries.stream().mapToDouble(DiaryEntryResponse::fatG).sum();
		return new DiaryDayResponse(date, entries, round(kcal), round(p), round(c), round(f));
	}

	private UserAccount requireEleve(Long id) {
		return users.findByIdAndRole(id, Role.ELEVE)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Élève introuvable"));
	}

	private static double round(double v) {
		return Math.round(v * 10.0) / 10.0;
	}
}
