package com.shakeup.backend.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shakeup.backend.config.OffProperties;
import com.shakeup.backend.domain.CatalogSource;
import com.shakeup.backend.domain.Food;
import com.shakeup.backend.integration.off.OffClient;
import com.shakeup.backend.integration.off.OffClient.Product;
import com.shakeup.backend.integration.off.OffClient.SearchPage;
import com.shakeup.backend.repository.FoodRepository;
import com.shakeup.backend.web.dto.FoodResponse;
import com.shakeup.backend.web.dto.OffSyncResult;

@Service
public class OffSyncService {

	private static final Logger log = LoggerFactory.getLogger(OffSyncService.class);

	private static final List<String> SEED_QUERIES = List.of(
			"poulet",
			"riz",
			"avoine",
			"oeuf",
			"fromage blanc",
			"banane",
			"saumon",
			"brocoli",
			"pain complet",
			"huile olive",
			"yaourt grec",
			"patate douce",
			"thon",
			"amandes",
			"pomme");

	private final OffClient client;
	private final FoodRepository foods;
	private final OffProperties properties;

	public OffSyncService(OffClient client, FoodRepository foods, OffProperties properties) {
		this.client = client;
		this.foods = foods;
		this.properties = properties;
	}

	public OffSyncResult syncSeedQueries() {
		int created = 0;
		int updated = 0;
		int skipped = 0;
		int fetched = 0;
		try {
			for (String query : SEED_QUERIES) {
				SearchPage page = client.search(query, properties.getPageSize());
				if (page == null || page.products() == null) {
					continue;
				}
				for (Product product : page.products()) {
					fetched++;
					SyncAction action = upsert(product);
					switch (action) {
						case CREATED -> created++;
						case UPDATED -> updated++;
						case SKIPPED -> skipped++;
					}
				}
				Thread.sleep(350);
			}
		} catch (Exception ex) {
			log.warn("OFF sync interrompue ({}): {}", ex.getClass().getSimpleName(), ex.getMessage());
		}
		log.info("OFF sync done: fetched={} created={} updated={} skipped={}", fetched, created, updated, skipped);
		return new OffSyncResult(created, updated, skipped, fetched);
	}

	public List<FoodResponse> searchAndImport(String query) {
		String q = query == null ? "" : query.trim();
		if (q.length() < 2) {
			return List.of();
		}
		try {
			SearchPage page = client.search(q, Math.min(properties.getPageSize(), 24));
			if (page == null || page.products() == null) {
				return foods.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
						.map(FoodResponse::from)
						.toList();
			}
			return page.products().stream()
					.map(this::upsertReturning)
					.filter(f -> f != null)
					.map(FoodResponse::from)
					.toList();
		} catch (Exception ex) {
			log.warn("OFF search fallback local: {}", ex.getMessage());
			return foods.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
					.map(FoodResponse::from)
					.toList();
		}
	}

	public FoodResponse importByBarcode(String rawCode) {
		String code = normalizeBarcode(rawCode);
		if (code == null) {
			return null;
		}
		Food local = foods.findBySourceAndExternalId(CatalogSource.OFF, code).orElse(null);
		if (local != null) {
			return FoodResponse.from(local);
		}
		try {
			var page = client.fetchByBarcode(code);
			if (page == null || page.status() != 1 || page.product() == null) {
				return null;
			}
			Product raw = page.product();
			Product product = new Product(
					code,
					raw.product_name(),
					raw.product_name_fr(),
					raw.nutriments(),
					raw.image_front_small_url(),
					raw.serving_size(),
					raw.serving_quantity());
			Food saved = upsertReturning(product);
			return saved == null ? null : FoodResponse.from(saved);
		} catch (Exception ex) {
			log.warn("OFF barcode fallback local: {}", ex.getMessage());
			return foods.findBySourceAndExternalId(CatalogSource.OFF, code)
					.map(FoodResponse::from)
					.orElse(null);
		}
	}

	static String normalizeBarcode(String raw) {
		if (raw == null) {
			return null;
		}
		String digits = raw.trim().replaceAll("[^0-9]", "");
		if (digits.length() < 8 || digits.length() > 14) {
			return null;
		}
		return digits;
	}

	private Food upsertReturning(Product product) {
		SyncAction action = upsert(product);
		if (action == SyncAction.SKIPPED && (product.code() == null || product.code().isBlank())) {
			return null;
		}
		return foods.findBySourceAndExternalId(CatalogSource.OFF, product.code()).orElse(null);
	}

	private SyncAction upsert(Product product) {
		if (product == null || product.code() == null || product.code().isBlank()) {
			return SyncAction.SKIPPED;
		}
		String name = pickName(product);
		Macros macros = pickMacros(product.nutriments());
		if (name == null || macros == null) {
			return SyncAction.SKIPPED;
		}

		Food existing = foods.findBySourceAndExternalId(CatalogSource.OFF, product.code()).orElse(null);
		String image = blankToNull(product.image_front_small_url());
		Double serving = pickServingGrams(product);
		if (serving == null) {
			serving = FoodServingHints.estimateGrams(name);
		}

		if (existing == null) {
			Food f = new Food();
			f.setName(name);
			f.setKcalPer100g(macros.kcal);
			f.setProteinPer100g(macros.protein);
			f.setCarbsPer100g(macros.carbs);
			f.setFatPer100g(macros.fat);
			f.setImageUrl(image);
			f.setServingGrams(serving);
			f.setExternalId(product.code());
			f.setSource(CatalogSource.OFF);
			foods.save(f);
			return SyncAction.CREATED;
		}

		boolean dirty = false;
		if (!name.equals(existing.getName())) {
			existing.setName(name);
			dirty = true;
		}
		if (macros.kcal != existing.getKcalPer100g()) {
			existing.setKcalPer100g(macros.kcal);
			dirty = true;
		}
		if (macros.protein != existing.getProteinPer100g()) {
			existing.setProteinPer100g(macros.protein);
			dirty = true;
		}
		if (macros.carbs != existing.getCarbsPer100g()) {
			existing.setCarbsPer100g(macros.carbs);
			dirty = true;
		}
		if (macros.fat != existing.getFatPer100g()) {
			existing.setFatPer100g(macros.fat);
			dirty = true;
		}
		if (image != null && !image.equals(existing.getImageUrl())) {
			existing.setImageUrl(image);
			dirty = true;
		}
		if (serving != null
				&& (existing.getServingGrams() == null || !serving.equals(existing.getServingGrams()))) {
			existing.setServingGrams(serving);
			dirty = true;
		}
		if (dirty) {
			foods.save(existing);
			return SyncAction.UPDATED;
		}
		return SyncAction.SKIPPED;
	}

	static Double pickServingGrams(Product product) {
		Double fromSize = parseGramsInText(product.serving_size());
		if (fromSize != null) {
			return fromSize;
		}
		Double qty = numObj(product.serving_quantity());
		if (qty != null && qty > 0 && qty <= 2000) {
			String size = product.serving_size();
			if (size != null && size.toLowerCase().contains("g") && !size.toLowerCase().contains("kg")) {
				return qty;
			}
		}
		return null;
	}

	static Double parseGramsInText(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		var matcher = java.util.regex.Pattern.compile("(\\d+(?:[.,]\\d+)?)\\s*g\\b", java.util.regex.Pattern.CASE_INSENSITIVE)
				.matcher(raw);
		if (!matcher.find()) {
			return null;
		}
		try {
			double value = Double.parseDouble(matcher.group(1).replace(',', '.'));
			return value > 0 && value <= 2000 ? value : null;
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	static Double numObj(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number number) {
			return number.doubleValue();
		}
		try {
			return Double.parseDouble(value.toString().trim().replace(',', '.'));
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	static String pickName(Product product) {
		if (product.product_name_fr() != null && !product.product_name_fr().isBlank()) {
			return product.product_name_fr().trim();
		}
		if (product.product_name() != null && !product.product_name().isBlank()) {
			return product.product_name().trim();
		}
		return null;
	}

	static Macros pickMacros(Map<String, Object> nutriments) {
		if (nutriments == null || nutriments.isEmpty()) {
			return null;
		}
		Double kcal = num(nutriments, "energy-kcal_100g");
		if (kcal == null) {
			Double kj = num(nutriments, "energy-kj_100g");
			if (kj != null) {
				kcal = kj / 4.184;
			}
		}
		Double protein = num(nutriments, "proteins_100g");
		Double carbs = num(nutriments, "carbohydrates_100g");
		Double fat = num(nutriments, "fat_100g");
		if (kcal == null || protein == null || carbs == null || fat == null) {
			return null;
		}
		return new Macros(round1(kcal), round1(protein), round1(carbs), round1(fat));
	}

	private static Double num(Map<String, Object> map, String key) {
		Object v = map.get(key);
		if (v instanceof Number n) {
			return n.doubleValue();
		}
		if (v instanceof String s && !s.isBlank()) {
			try {
				return Double.parseDouble(s.replace(',', '.'));
			} catch (NumberFormatException ignored) {
				return null;
			}
		}
		return null;
	}

	private static double round1(double v) {
		return Math.round(v * 10.0) / 10.0;
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

	private record Macros(double kcal, double protein, double carbs, double fat) {
	}

	private enum SyncAction {
		CREATED,
		UPDATED,
		SKIPPED
	}
}
