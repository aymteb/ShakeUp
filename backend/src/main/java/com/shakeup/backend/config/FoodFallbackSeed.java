package com.shakeup.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Food;
import com.shakeup.backend.repository.FoodRepository;

@Component
@Order(5)
public class FoodFallbackSeed implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(FoodFallbackSeed.class);

	private final FoodRepository foods;

	public FoodFallbackSeed(FoodRepository foods) {
		this.foods = foods;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (foods.count() > 0) {
			return;
		}
		log.warn("Aucun aliment en base après OFF — fallback local minimal");
		save("Poulet (blanc)", 110, 23, 0, 1.5, null);
		save("Riz blanc cuit", 130, 2.7, 28, 0.3, null);
		save("Flocons d'avoine", 370, 13, 59, 7, null);
		save("Oeuf entier", 140, 12.5, 0.7, 9.5, 60.0);
		save("Fromage blanc 0%", 50, 8, 4, 0.2, null);
		save("Banane", 90, 1.1, 20, 0.3, 120.0);
		save("Saumon", 200, 20, 0, 13, null);
		save("Brocoli", 35, 2.8, 4, 0.4, null);
		save("Pain complet", 250, 9, 41, 4, null);
		save("Huile d'olive", 900, 0, 0, 100, null);
		save("Yaourt grec", 100, 9, 4, 5, null);
		save("Patate douce", 90, 1.6, 20, 0.1, null);
	}

	private void save(String name, double kcal, double p, double c, double f, Double servingGrams) {
		Food food = new Food();
		food.setName(name);
		food.setKcalPer100g(kcal);
		food.setProteinPer100g(p);
		food.setCarbsPer100g(c);
		food.setFatPer100g(f);
		food.setServingGrams(servingGrams);
		food.setSource(CatalogSource.SEED);
		foods.save(food);
	}
}
