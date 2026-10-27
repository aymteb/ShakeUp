package com.shakeup.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.repository.ExerciseRepository;
import com.shakeup.backend.service.WgerSyncService;

@Component
@Order(3)
public class WgerSyncOnStartup implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(WgerSyncOnStartup.class);

	private final WgerProperties properties;
	private final ExerciseRepository exercises;
	private final WgerSyncService syncService;

	public WgerSyncOnStartup(
			WgerProperties properties,
			ExerciseRepository exercises,
			WgerSyncService syncService) {
		this.properties = properties;
		this.exercises = exercises;
		this.syncService = syncService;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!properties.isSyncOnStartupIfEmpty()) {
			return;
		}
		if (exercises.countBySource(CatalogSource.WGER) > 0) {
			return;
		}
		log.info("Catalogue Wger vide — sync initiale depuis {}", properties.getBaseUrl());
		syncService.syncAll();
	}
}
