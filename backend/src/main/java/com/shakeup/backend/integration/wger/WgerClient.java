package com.shakeup.backend.integration.wger;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shakeup.backend.config.WgerProperties;

@Component
public class WgerClient {

	private final RestClient client;
	private final WgerProperties properties;

	public WgerClient(RestClient.Builder builder, WgerProperties properties) {
		this.properties = properties;
		this.client = builder
				.baseUrl(properties.getBaseUrl())
				.defaultHeader("Accept", "application/json")
				.defaultHeader("User-Agent", "ShakeUp/1.0 (portfolio; coach catalog sync)")
				.build();
	}

	public Page fetchExerciseInfoPage(int offset) {
		return client.get()
				.uri(uri -> uri
						.path("/exerciseinfo/")
						.queryParam("limit", properties.getPageSize())
						.queryParam("offset", offset)
						.build())
				.retrieve()
				.body(Page.class);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Page(int count, String next, List<ExerciseInfo> results) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ExerciseInfo(long id, Category category, List<Translation> translations, List<Image> images) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Category(int id, String name) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Translation(int language, String name, String description) {
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Image(String image, boolean isMain) {
	}
}
