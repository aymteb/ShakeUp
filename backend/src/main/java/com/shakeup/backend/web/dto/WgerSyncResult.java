package com.shakeup.backend.web.dto;

public record WgerSyncResult(int created, int updated, int skipped, int totalFetched) {
}
