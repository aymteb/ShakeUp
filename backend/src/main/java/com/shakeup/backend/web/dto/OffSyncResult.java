package com.shakeup.backend.web.dto;

public record OffSyncResult(int created, int updated, int skipped, int totalFetched) {
}
