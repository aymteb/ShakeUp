package com.shakeup.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.repository.FoodRepository;
import com.shakeup.backend.service.OffSyncService;

@Component
@Order(3)
public class OffSyncOnStartup implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(OffSyncOnStartup.class);

	private final OffProperties properties;
	private final FoodRepository foods;
	private final OffSyncService syncService;

	public OffSyncOnStartup(OffProperties properties, FoodRepository foods, OffSyncService syncService) {
		this.properties = properties;
		this.foods = foods;
		this.syncService = syncService;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!properties.isSyncOnStartupIfEmpty()) {
			return;
		}
		if (foods.countBySource(CatalogSource.OFF) > 0) {
			return;
		}
		log.info("Catalogue OFF vide — sync initiale depuis {}", properties.getBaseUrl());
		try {
			syncService.syncSeedQueries();
		} catch (Exception ex) {
			log.warn("Sync OFF au démarrage échouée : {}", ex.getMessage());
		}
	}
}
