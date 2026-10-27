package com.shakeup.backend.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shakeup.backend.config.WgerProperties;
import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Exercise;
import com.shakeup.backend.domain.ExerciseKind;
import com.shakeup.backend.integration.wger.WgerClient;
import com.shakeup.backend.integration.wger.WgerClient.ExerciseInfo;
import com.shakeup.backend.integration.wger.WgerClient.Image;
import com.shakeup.backend.integration.wger.WgerClient.Page;
import com.shakeup.backend.integration.wger.WgerClient.Translation;
import com.shakeup.backend.repository.ExerciseRepository;
import com.shakeup.backend.web.dto.WgerSyncResult;

@Service
public class WgerSyncService {

	private static final Logger log = LoggerFactory.getLogger(WgerSyncService.class);
	private static final int LANG_FR = 12;
	private static final int LANG_EN = 2;

	private static final Map<String, String> MUSCLE_FR = Map.of(
			"Abs", "Core",
			"Arms", "Bras",
			"Back", "Dos",
			"Calves", "Mollets",
			"Cardio", "Cardio",
			"Chest", "Pectoraux",
			"Legs", "Jambes",
			"Shoulders", "Épaules");

	private final WgerClient client;
	private final ExerciseRepository exercises;
	private final WgerProperties properties;

	public WgerSyncService(WgerClient client, ExerciseRepository exercises, WgerProperties properties) {
		this.client = client;
		this.exercises = exercises;
		this.properties = properties;
	}

	public WgerSyncResult syncAll() {
		int created = 0;
		int updated = 0;
		int skipped = 0;
		int fetched = 0;
		int offset = 0;

		while (true) {
			Page page = client.fetchExerciseInfoPage(offset);
			if (page == null || page.results() == null || page.results().isEmpty()) {
				break;
			}
			for (ExerciseInfo info : page.results()) {
				fetched++;
				SyncAction action = upsert(info);
				switch (action) {
					case CREATED -> created++;
					case UPDATED -> updated++;
					case SKIPPED -> skipped++;
				}
			}
			if (page.next() == null || page.next().isBlank()) {
				break;
			}
			offset += properties.getPageSize();
		}

		log.info("Wger sync done: fetched={} created={} updated={} skipped={}", fetched, created, updated, skipped);
		return new WgerSyncResult(created, updated, skipped, fetched);
	}

	private SyncAction upsert(ExerciseInfo info) {
		Translation translation = pickTranslation(info.translations());
		if (translation == null || translation.name() == null || translation.name().isBlank()) {
			return SyncAction.SKIPPED;
		}

		String name = translation.name().trim();
		String muscle = mapMuscle(info.category() != null ? info.category().name() : null);
		ExerciseKind kind = detectKind(name);
		String demoUrl = pickImage(info.images());

		Exercise existing = exercises
				.findBySourceAndExternalId(CatalogSource.WGER, info.id())
				.orElse(null);

		if (existing == null) {
			Exercise e = new Exercise();
			e.setName(name);
			e.setMuscleGroup(muscle);
			e.setKind(kind);
			e.setDemoUrl(demoUrl);
			e.setExternalId(info.id());
			e.setSource(CatalogSource.WGER);
			e.setHidden(false);
			exercises.save(e);
			return SyncAction.CREATED;
		}

		boolean dirty = false;
		if (!name.equals(existing.getName())) {
			existing.setName(name);
			dirty = true;
		}
		if (!muscle.equals(existing.getMuscleGroup())) {
			existing.setMuscleGroup(muscle);
			dirty = true;
		}
		if (kind != existing.getKind()) {
			existing.setKind(kind);
			dirty = true;
		}
		if (demoUrl != null && !demoUrl.equals(existing.getDemoUrl())) {
			existing.setDemoUrl(demoUrl);
			dirty = true;
		}
		if (dirty) {
			exercises.save(existing);
			return SyncAction.UPDATED;
		}
		return SyncAction.SKIPPED;
	}

	static Translation pickTranslation(List<Translation> translations) {
		if (translations == null || translations.isEmpty()) {
			return null;
		}
		Translation fr = null;
		Translation en = null;
		for (Translation t : translations) {
			if (t.language() == LANG_FR && fr == null) {
				fr = t;
			} else if (t.language() == LANG_EN && en == null) {
				en = t;
			}
		}
		if (fr != null) {
			return fr;
		}
		if (en != null) {
			return en;
		}
		return translations.getFirst();
	}

	static String mapMuscle(String category) {
		if (category == null || category.isBlank()) {
			return "Autre";
		}
		return MUSCLE_FR.getOrDefault(category, category);
	}

	static ExerciseKind detectKind(String name) {
		String n = name.toLowerCase(Locale.ROOT);
		if (n.contains("plank")
				|| n.contains("gainage")
				|| n.contains("hold")
				|| n.contains("isometric")
				|| n.contains("wall sit")
				|| n.contains("assis mural")) {
			return ExerciseKind.HOLD;
		}
		return ExerciseKind.LOAD;
	}

	static String pickImage(List<Image> images) {
		if (images == null || images.isEmpty()) {
			return null;
		}
		for (Image img : images) {
			if (img.isMain() && img.image() != null && !img.image().isBlank()) {
				return img.image();
			}
		}
		String first = images.getFirst().image();
		return first != null && !first.isBlank() ? first : null;
	}

	private enum SyncAction {
		CREATED,
		UPDATED,
		SKIPPED
	}
}
