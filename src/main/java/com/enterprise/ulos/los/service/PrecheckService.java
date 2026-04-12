package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.CustomerEntity;
import com.enterprise.ulos.los.entity.ApplicationWorkspaceEntity;
import com.enterprise.ulos.los.entity.PrecheckSessionEntity;
import com.enterprise.ulos.los.model.PrecheckApiModels;
import com.enterprise.ulos.los.repository.ApplicationWorkspaceRepository;
import com.enterprise.ulos.los.repository.PrecheckSessionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PrecheckService {

    private static final DateTimeFormatter REF_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final PrecheckSessionRepository precheckSessionRepository;
    private final ApplicationWorkspaceRepository applicationWorkspaceRepository;
    private final CustomerPortfolioService customerPortfolioService;
    private final JsonSectionMapper jsonSectionMapper;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${los.precheck.surrounding.url:}")
    private String surroundingBaseUrl;

    public PrecheckService(
            PrecheckSessionRepository precheckSessionRepository,
            ApplicationWorkspaceRepository applicationWorkspaceRepository,
            CustomerPortfolioService customerPortfolioService,
            JsonSectionMapper jsonSectionMapper,
            ObjectMapper objectMapper
    ) {
        this.precheckSessionRepository = precheckSessionRepository;
        this.applicationWorkspaceRepository = applicationWorkspaceRepository;
        this.customerPortfolioService = customerPortfolioService;
        this.jsonSectionMapper = jsonSectionMapper;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public PrecheckApiModels.PrecheckResponse save(PrecheckApiModels.PrecheckRequest request) {
        validateRequest(request);
        String type = request.checkType().trim().toUpperCase(Locale.ROOT);
        String checkRef = request.checkRef() == null || request.checkRef().isBlank()
                ? generateCheckRef(type)
                : request.checkRef().trim();

        PrecheckSessionEntity entity = precheckSessionRepository.findByCheckRef(checkRef).orElseGet(PrecheckSessionEntity::new);
        CustomerEntity customer = customerPortfolioService.findEntityByCif(request.customerCif().trim());

        entity.setCheckRef(checkRef);
        entity.setCheckType(type);
        entity.setCustomer(customer);
        entity.setStatus(defaultValue(request.status(), "REQUESTED").toUpperCase(Locale.ROOT));
        entity.setRequestedAt(request.requestedAt() == null ? LocalDateTime.now() : request.requestedAt());
        entity.setCompletedAt(request.completedAt());
        entity.setResultSummary(request.resultSummary());
        entity.setResultJson(jsonSectionMapper.toJson(request.resultData()));
        entity.setLinkedApplicationId(request.linkedApplicationId());
        entity.setNotes(request.notes());

        return toResponse(precheckSessionRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public PrecheckApiModels.PrecheckPageResponse listPage(String type, String customerCif, String keyword, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        String normalizedType = normalize(type);
        String normalizedCustomerCif = normalize(customerCif);
        String normalizedKeyword = normalize(keyword);

        Page<PrecheckSessionEntity> result = precheckSessionRepository.search(
                normalizedType,
                normalizedCustomerCif,
                normalizedKeyword,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"))
        );

        return new PrecheckApiModels.PrecheckPageResponse(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Transactional(readOnly = true)
    public PrecheckApiModels.PrecheckResponse getByCheckRef(String checkRef) {
        if (checkRef == null || checkRef.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkRef is required");
        }
        return toResponse(precheckSessionRepository.findByCheckRef(checkRef.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pre-check not found: " + checkRef)));
    }

    @Transactional(readOnly = true)
    public PrecheckApiModels.LatestApplicationSourceResponse latestSource(String type, String customerCif) {
        String normalizedType = normalizeCheckType(type);
        if (customerCif == null || customerCif.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerCif is required");
        }
        String normalizedCif = customerCif.trim();
        CustomerEntity customer = customerPortfolioService.findEntityByCif(normalizedCif);

        Optional<ApplicationWorkspaceEntity> latest = applicationWorkspaceRepository
                .findTopByCustomer_CifNumberOrderByUpdatedAtDesc(normalizedCif);

        String sourceId = null;
        LocalDateTime sourceUpdatedAt = null;
        List<PrecheckApiModels.SourceItem> items = List.of();

        if (latest.isPresent()) {
            ApplicationWorkspaceEntity application = latest.get();
            sourceId = application.getApplicationId();
            sourceUpdatedAt = application.getUpdatedAt();
            items = "SLIK".equals(normalizedType)
                    ? extractSlikSourceItems(application)
                    : extractCollateralSourceItems(application);
        }

        if (items.isEmpty()) {
            items = "SLIK".equals(normalizedType)
                    ? extractSlikSourceItemsFromPortfolio(customer)
                    : extractCollateralSourceItemsFromPortfolio(customer);
            if (!items.isEmpty()) {
                sourceId = "PORTFOLIO-" + customer.getCifNumber();
                sourceUpdatedAt = customer.getUpdatedAt();
            }
        }

        return new PrecheckApiModels.LatestApplicationSourceResponse(
                sourceId,
                sourceUpdatedAt,
                items
        );
    }

    public PrecheckApiModels.PrecheckResponse requestNew(PrecheckApiModels.RequestNewPrecheckRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request payload is required");
        }
        String type = normalizeCheckType(request.checkType());
        if (request.customerCif() == null || request.customerCif().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerCif is required");
        }

        ArrayNode selectedItems = jsonSectionMapper.ensureArray(request.items());
        if (selectedItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one subject/collateral item is required");
        }

        CustomerEntity customer = customerPortfolioService.findEntityByCif(request.customerCif().trim());
        PrecheckSessionEntity entity = new PrecheckSessionEntity();
        entity.setCheckRef(generateCheckRef(type));
        entity.setCheckType(type);
        entity.setCustomer(customer);
        entity.setStatus("REQUESTED");
        entity.setRequestedAt(LocalDateTime.now());
        entity.setCompletedAt(null);
        entity.setLinkedApplicationId(request.sourceApplicationId());
        entity.setResultSummary("SLIK".equals(type) ? "SLIK request submitted to surrounding system" : "Collateral appraisal request submitted to surrounding system");
        entity.setNotes(request.notes());

        ObjectNode data = objectMapper.createObjectNode();
        if ("SLIK".equals(type)) {
            data.set("subjects", selectedItems);
        } else {
            data.set("collaterals", selectedItems);
        }
        data.put("sourceApplicationId", defaultValue(request.sourceApplicationId(), ""));
        data.put("requestedBy", "RM");
        data.put("requestedAt", LocalDateTime.now().toString());

        SurroundingDispatchResult dispatchResult = dispatchToSurrounding(type, customer, entity.getCheckRef(), data);
        if (dispatchResult.dispatched()) {
            entity.setStatus("IN_PROGRESS");
            entity.setNotes(appendNotes(entity.getNotes(), "Surrounding ACK: " + dispatchResult.message()));
        } else {
            entity.setNotes(appendNotes(entity.getNotes(), "Surrounding pending: " + dispatchResult.message()));
        }
        entity.setResultJson(jsonSectionMapper.toJson(data));

        return toResponse(precheckSessionRepository.save(entity));
    }

    private void validateRequest(PrecheckApiModels.PrecheckRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pre-check payload is required");
        }
        normalizeCheckType(request.checkType());
        if (request.customerCif() == null || request.customerCif().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerCif is required");
        }
    }

    private String normalizeCheckType(String type) {
        if (type == null || type.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkType is required");
        }
        String normalized = type.trim().toUpperCase(Locale.ROOT);
        if (!"SLIK".equals(normalized) && !"COLLATERAL".equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkType must be SLIK or COLLATERAL");
        }
        return normalized;
    }

    private String generateCheckRef(String type) {
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
        return type + "-" + LocalDateTime.now().format(REF_DATE) + "-" + suffix;
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private PrecheckApiModels.PrecheckResponse toResponse(PrecheckSessionEntity entity) {
        return new PrecheckApiModels.PrecheckResponse(
                entity.getId(),
                entity.getCheckRef(),
                entity.getCheckType(),
                entity.getCustomer().getCifNumber(),
                entity.getCustomer().getCompanyName(),
                entity.getStatus(),
                entity.getRequestedAt(),
                entity.getCompletedAt(),
                entity.getResultSummary(),
                parseNode(entity.getResultJson()),
                entity.getLinkedApplicationId(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private JsonNode parseNode(String value) {
        return jsonSectionMapper.fromJson(value);
    }

    private List<PrecheckApiModels.SourceItem> extractSlikSourceItems(ApplicationWorkspaceEntity application) {
        List<PrecheckApiModels.SourceItem> items = new ArrayList<>();
        Map<String, Boolean> seen = new LinkedHashMap<>();

        items.add(
                new PrecheckApiModels.SourceItem(
                        "MAIN-" + application.getCustomer().getCifNumber(),
                        application.getCustomer().getCompanyName(),
                        "Main Borrower",
                        "NPWP",
                        defaultValue(application.getCustomer().getTaxId(), application.getCustomer().getCifNumber()),
                        null,
                        "Borrower",
                        null
                )
        );

        JsonNode slik = parseNode(application.getSlikJson());
        for (JsonNode item : jsonSectionMapper.ensureArray(slik.path("subjects"))) {
            String name = defaultValue(item.path("name").asText(), "-");
            String idNo = defaultValue(item.path("idNo").asText(), name + UUID.randomUUID().toString().substring(0, 4));
            String dedupeKey = (name + "|" + idNo).toLowerCase(Locale.ROOT);
            if (seen.containsKey(dedupeKey)) {
                continue;
            }
            seen.put(dedupeKey, Boolean.TRUE);
            items.add(
                    new PrecheckApiModels.SourceItem(
                            "SUB-" + idNo,
                            name,
                            defaultValue(item.path("relationship").asText(), "Related Party"),
                            defaultValue(item.path("idType").asText(), "ID"),
                            idNo,
                            null,
                            null,
                            toRaw(item)
                    )
            );
        }

        JsonNode setup = parseNode(application.getSetupJson());
        for (String key : List.of("relatedParties", "related_parties", "thirdParties", "third_parties", "subjects")) {
            for (JsonNode item : jsonSectionMapper.ensureArray(setup.path(key))) {
                String name = defaultValue(item.path("name").asText(), item.path("fullName").asText());
                if (name.isBlank()) {
                    continue;
                }
                String idNo = defaultValue(item.path("idNo").asText(), item.path("idNumber").asText());
                String dedupeKey = (name + "|" + idNo).toLowerCase(Locale.ROOT);
                if (seen.containsKey(dedupeKey)) {
                    continue;
                }
                seen.put(dedupeKey, Boolean.TRUE);
                items.add(
                        new PrecheckApiModels.SourceItem(
                                "SET-" + (idNo.isBlank() ? UUID.randomUUID().toString().substring(0, 6) : idNo),
                                name,
                                defaultValue(item.path("relationship").asText(), "Related Party"),
                                defaultValue(item.path("idType").asText(), "ID"),
                                idNo,
                                null,
                                null,
                                toRaw(item)
                        )
                );
            }
        }

        return items;
    }

    private List<PrecheckApiModels.SourceItem> extractCollateralSourceItems(ApplicationWorkspaceEntity application) {
        List<PrecheckApiModels.SourceItem> items = new ArrayList<>();
        JsonNode collateralsRoot = parseNode(application.getCollateralsJson());
        ArrayNode collaterals;
        if (collateralsRoot.isObject() && collateralsRoot.has("collaterals")) {
            collaterals = jsonSectionMapper.ensureArray(collateralsRoot.path("collaterals"));
        } else if (collateralsRoot.isObject() && collateralsRoot.size() == 0) {
            collaterals = objectMapper.createArrayNode();
        } else {
            collaterals = jsonSectionMapper.ensureArray(collateralsRoot);
        }

        for (JsonNode item : collaterals) {
            if (item == null || item.isNull() || (item.isObject() && item.size() == 0)) {
                continue;
            }
            String code = defaultValue(item.path("code").asText(), item.path("collateralId").asText());
            String type = defaultValue(item.path("type").asText(), item.path("collateralType").asText());
            String description = defaultValue(item.path("description").asText(), "Collateral");
            items.add(
                    new PrecheckApiModels.SourceItem(
                            "COL-" + (code.isBlank() ? UUID.randomUUID().toString().substring(0, 6) : code),
                            code.isBlank() ? description : code,
                            null,
                            null,
                            null,
                            type,
                            description,
                            toRaw(item)
                )
            );
        }

        if (!items.isEmpty()) {
            return items;
        }

        JsonNode setup = parseNode(application.getSetupJson());
        for (String key : List.of("collaterals", "collateralItems", "collateral_items")) {
            for (JsonNode item : jsonSectionMapper.ensureArray(setup.path(key))) {
                if (item == null || item.isNull() || (item.isObject() && item.size() == 0)) {
                    continue;
                }
                String code = defaultValue(item.path("code").asText(), item.path("collateralId").asText());
                String type = defaultValue(item.path("type").asText(), item.path("collateralType").asText());
                String description = defaultValue(item.path("description").asText(), "Collateral");
                items.add(
                        new PrecheckApiModels.SourceItem(
                                "COL-" + (code.isBlank() ? UUID.randomUUID().toString().substring(0, 6) : code),
                                code.isBlank() ? description : code,
                                null,
                                null,
                                null,
                                type,
                                description,
                                toRaw(item)
                        )
                );
            }
        }
        return items;
    }

    private List<PrecheckApiModels.SourceItem> extractSlikSourceItemsFromPortfolio(CustomerEntity customer) {
        List<PrecheckApiModels.SourceItem> items = new ArrayList<>();
        Map<String, Boolean> seen = new LinkedHashMap<>();
        items.add(
                new PrecheckApiModels.SourceItem(
                        "MAIN-" + customer.getCifNumber(),
                        customer.getCompanyName(),
                        "Main Borrower",
                        "NPWP",
                        defaultValue(customer.getTaxId(), customer.getCifNumber()),
                        null,
                        "Borrower",
                        null
                )
        );

        JsonNode profile = parseNode(customer.getProfileJson());
        for (String key : List.of("relatedParties", "related_parties", "subjects", "thirdParties", "third_parties")) {
            for (JsonNode item : jsonSectionMapper.ensureArray(profile.path(key))) {
                String name = defaultValue(item.path("name").asText(), item.path("fullName").asText());
                if (name.isBlank()) {
                    continue;
                }
                String idNo = defaultValue(item.path("idNo").asText(), item.path("idNumber").asText());
                String dedupeKey = (name + "|" + idNo).toLowerCase(Locale.ROOT);
                if (seen.containsKey(dedupeKey)) {
                    continue;
                }
                seen.put(dedupeKey, Boolean.TRUE);
                items.add(
                        new PrecheckApiModels.SourceItem(
                                "PRF-" + (idNo.isBlank() ? UUID.randomUUID().toString().substring(0, 6) : idNo),
                                name,
                                defaultValue(item.path("relationship").asText(), "Related Party"),
                                defaultValue(item.path("idType").asText(), "ID"),
                                idNo,
                                null,
                                null,
                                toRaw(item)
                        )
                );
            }
        }
        return items;
    }

    private List<PrecheckApiModels.SourceItem> extractCollateralSourceItemsFromPortfolio(CustomerEntity customer) {
        List<PrecheckApiModels.SourceItem> items = new ArrayList<>();
        JsonNode profile = parseNode(customer.getProfileJson());
        for (String key : List.of("collaterals", "collateralItems", "collateral_items")) {
            for (JsonNode item : jsonSectionMapper.ensureArray(profile.path(key))) {
                if (item == null || item.isNull() || (item.isObject() && item.size() == 0)) {
                    continue;
                }
                String code = defaultValue(item.path("code").asText(), item.path("collateralId").asText());
                String type = defaultValue(item.path("collateralType").asText(), item.path("type").asText());
                String description = defaultValue(item.path("description").asText(), "Collateral");
                items.add(
                        new PrecheckApiModels.SourceItem(
                                "COL-PRF-" + (code.isBlank() ? UUID.randomUUID().toString().substring(0, 6) : code),
                                code.isBlank() ? description : code,
                                null,
                                null,
                                null,
                                type,
                                description,
                                toRaw(item)
                        )
                );
            }
        }
        return items;
    }

    private SurroundingDispatchResult dispatchToSurrounding(String type, CustomerEntity customer, String checkRef, JsonNode data) {
        if (surroundingBaseUrl == null || surroundingBaseUrl.isBlank()) {
            return new SurroundingDispatchResult(false, "surrounding URL not configured");
        }

        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("checkRef", checkRef);
        payload.put("checkType", type);
        payload.put("customerCif", customer.getCifNumber());
        payload.put("customerName", customer.getCompanyName());
        payload.set("payload", data);

        try {
            String body = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(surroundingBaseUrl.endsWith("/") ? surroundingBaseUrl + "prechecks/request" : surroundingBaseUrl + "/prechecks/request"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return new SurroundingDispatchResult(true, "HTTP " + response.statusCode());
            }
            return new SurroundingDispatchResult(false, "HTTP " + response.statusCode() + " " + response.body());
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return new SurroundingDispatchResult(false, exception.getMessage());
        }
    }

    private String appendNotes(String existing, String append) {
        if (existing == null || existing.isBlank()) {
            return append;
        }
        return existing + "\n" + append;
    }

    private String toRaw(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    private record SurroundingDispatchResult(boolean dispatched, String message) {
    }
}
