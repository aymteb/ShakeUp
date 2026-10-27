package com.shakeup.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shakeup.off")
public class OffProperties {

	private String baseUrl = "https://world.openfoodfacts.org";
	private int pageSize = 20;
	private boolean syncOnStartupIfEmpty = true;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isSyncOnStartupIfEmpty() {
		return syncOnStartupIfEmpty;
	}

	public void setSyncOnStartupIfEmpty(boolean syncOnStartupIfEmpty) {
		this.syncOnStartupIfEmpty = syncOnStartupIfEmpty;
	}
}
