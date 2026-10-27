package com.shakeup.backend.integration.off;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shakeup.backend.config.OffProperties;

@Component
public class OffClient {

	private final RestClient client;
	private final OffProperties properties;

	public OffClient(RestClient.Builder builder, OffProperties properties) {
		this.properties = properties;
		this.client = builder
				.baseUrl(properties.getBaseUrl())
				.defaultHeader("Accept", "application/json")
				.defaultHeader("User-Agent", "ShakeUp/1.0 (portfolio; nutrition catalog; contact@shakeup.local)")
				.build();
	}

	public SearchPage search(String terms, int pageSize) {
		return client.get()
				.uri(uri -> uri
						.path("/api/v2/search")
						.queryParam("search_terms", terms)
						.queryParam("page_size", pageSize)
						.queryParam(
								"fields",
								"code,product_name,product_name_fr,nutriments,image_front_small_url,serving_size,serving_quantity")
						.build())
				.retrieve()
				.onStatus(status -> status.value() >= 400, (req, res) -> {
					throw new OffUnavailableException("OFF HTTP " + res.getStatusCode().value());
				})
				.body(SearchPage.class);
	}

	public ProductPage fetchByBarcode(String code) {
		return client.get()
				.uri(uri -> uri
						.path("/api/v2/product/{code}")
						.queryParam(
								"fields",
								"code,product_name,product_name_fr,nutriments,image_front_small_url,serving_size,serving_quantity")
						.build(code))
				.retrieve()
				.onStatus(status -> status.value() >= 400, (req, res) -> {
					throw new OffUnavailableException("OFF HTTP " + res.getStatusCode().value());
				})
				.body(ProductPage.class);
	}

	public static class OffUnavailableException extends RuntimeException {
		public OffUnavailableException(String message) {
			super(message);
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SearchPage(int count, List<Product> products) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ProductPage(int status, Product product) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Product(
			String code,
			String product_name,
			String product_name_fr,
			Map<String, Object> nutriments,
			String image_front_small_url,
			String serving_size,
			Object serving_quantity) {
	}
}

