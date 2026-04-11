package com.enterprise.ulos.los.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public final class MasterDataApiModels {

    private MasterDataApiModels() {
    }

    public record ProductResponse(
            String code,
            String name,
            String category
    ) {
    }

    public record FacilityResponse(
            String code,
            String name,
            String productCode,
            List<String> segments,
            boolean revolving,
            boolean funded,
            boolean nonFunded,
            JsonNode fieldMapping
    ) {
    }

    public record PortfolioLookupResponse(
            String masterType,
            String code,
            String name,
            String legacyCode,
            Integer sortOrder,
            boolean active,
            String description,
            JsonNode extraData
    ) {
    }

    public record PortfolioLookupUpsertRequest(
            String masterType,
            String code,
            String name,
            String legacyCode,
            Integer sortOrder,
            Boolean active,
            String description,
            JsonNode extraData
    ) {
    }

    public record PortfolioLookupPageResponse(
            List<PortfolioLookupResponse> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }
}
