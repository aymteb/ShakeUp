package com.shakeup.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shakeup.wger")
public class WgerProperties {

	private String baseUrl = "https://wger.de/api/v2";
	private int pageSize = 50;
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
