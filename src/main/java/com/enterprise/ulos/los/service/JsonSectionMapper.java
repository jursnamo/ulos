package com.enterprise.ulos.los.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

@Component
public class JsonSectionMapper {

    private final ObjectMapper objectMapper;

    public JsonSectionMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid JSON payload", exception);
        }
    }

    public JsonNode fromJson(String value) {
        if (value == null || value.isBlank()) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(value);
        } catch (IOException exception) {
            throw new IllegalStateException("Stored JSON cannot be parsed", exception);
        }
    }

    public ArrayNode ensureArray(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return objectMapper.createArrayNode();
        }
        if (node.isArray()) {
            return (ArrayNode) node;
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add(node);
        return arrayNode;
    }

    public ObjectNode ensureObject(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return objectMapper.createObjectNode();
        }
        if (node.isObject()) {
            return (ObjectNode) node;
        }
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.set("value", node);
        return objectNode;
    }

    public BigDecimal sumExposure(JsonNode facilitiesNode) {
        BigDecimal result = BigDecimal.ZERO;
        ArrayNode facilities = ensureArray(facilitiesNode);
        for (JsonNode item : facilities) {
            result = result.add(extractExposureValue(item));
        }
        return result;
    }

    private BigDecimal extractExposureValue(JsonNode facilityNode) {
        for (String key : new String[]{"amount", "limit", "limitAmount", "proposed_limit", "proposedLimit"}) {
            if (facilityNode.hasNonNull(key)) {
                JsonNode valueNode = facilityNode.get(key);
                if (valueNode.isNumber()) {
                    return valueNode.decimalValue();
                }
                if (valueNode.isTextual()) {
                    String normalized = valueNode.textValue().replace(",", "").replace("$", "").trim();
                    if (!normalized.isBlank()) {
                        try {
                            return new BigDecimal(normalized);
                        } catch (NumberFormatException ignored) {
                            // Continue fallback attempts.
                        }
                    }
                }
            }
        }

        if (facilityNode.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = facilityNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getValue().isNumber() && field.getKey().toLowerCase().contains("amount")) {
                    return field.getValue().decimalValue();
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
