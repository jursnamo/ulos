package com.enterprise.ulos.los.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public final class CustomerApiModels {

    private CustomerApiModels() {
    }

    public record CustomerRequest(
            String cifNumber,
            String companyName,
            String legalName,
            String companyType,
            LocalDate registrationDate,
            String taxId,
            String sector,
            String location,
            String status,
            JsonNode profileData
    ) {
    }

    public record CustomerSummaryResponse(
            String cifNumber,
            String companyName,
            String sector,
            String location,
            String status,
            LocalDateTime updatedAt
    ) {
    }

    public record CustomerSummaryPageResponse(
            java.util.List<CustomerSummaryResponse> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    public record CustomerDetailResponse(
            Long id,
            String cifNumber,
            String companyName,
            String legalName,
            String companyType,
            LocalDate registrationDate,
            String taxId,
            String sector,
            String location,
            String status,
            JsonNode profileData,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
