package com.enterprise.ulos.los.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.List;

public final class PrecheckApiModels {

    private PrecheckApiModels() {
    }

    public record PrecheckRequest(
            String checkRef,
            String checkType,
            String customerCif,
            String status,
            LocalDateTime requestedAt,
            LocalDateTime completedAt,
            String resultSummary,
            JsonNode resultData,
            String linkedApplicationId,
            String notes
    ) {
    }

    public record PrecheckResponse(
            Long id,
            String checkRef,
            String checkType,
            String customerCif,
            String customerName,
            String status,
            LocalDateTime requestedAt,
            LocalDateTime completedAt,
            String resultSummary,
            JsonNode resultData,
            String linkedApplicationId,
            String notes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record PrecheckPageResponse(
            List<PrecheckResponse> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    public record SourceItem(
            String key,
            String label,
            String relationship,
            String idType,
            String idNo,
            String collateralType,
            String description,
            String rawJson
    ) {
    }

    public record LatestApplicationSourceResponse(
            String applicationId,
            LocalDateTime updatedAt,
            List<SourceItem> items
    ) {
    }

    public record RequestNewPrecheckRequest(
            String checkType,
            String customerCif,
            String sourceApplicationId,
            JsonNode items,
            String notes
    ) {
    }
}
