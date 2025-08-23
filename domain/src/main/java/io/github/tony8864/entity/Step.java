package io.github.tony8864.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step {
    private final String description;

    private final String endpointName;
    private final String dataName;

    // Request customizations
    private final Map<String, String> pathParams = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private final Map<String, Object> bodyOverrides = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    // Response handling
    private final List<Extractor> extractors = new ArrayList<>();
    private Integer expectedStatus;

    private Step(String description, String endpointName, String dataName) {
        this.description = description;
        this.endpointName = endpointName;
        this.dataName = dataName;
    }

    public static Step of(String description, String endpointName, String dataName) {
        return new Step(description, endpointName, dataName);
    }

    // --- DSL methods ---
    public Step withPathParam(String key, String value) {
        pathParams.put(key, value);
        return this;
    }

    public Step withQueryParam(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    public Step withHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Step withBodyOverride(String field, Object value) {
        bodyOverrides.put(field, value);
        return this;
    }

    public Step saveAs(String variableName, String jsonPath) {
        extractors.add(new Extractor(variableName, jsonPath));
        return this;
    }

    public Step expectStatus(int statusCode) {
        this.expectedStatus = statusCode;
        return this;
    }

    // --- Getters (for ScenarioExecutor) ---
    public String getDescription() { return description; }
    public String getEndpointName() { return endpointName; }
    public String getDataName() { return dataName; }
    public Map<String, String> getPathParams() { return pathParams; }
    public Map<String, String> getQueryParams() { return queryParams; }
    public Map<String, Object> getBodyOverrides() { return bodyOverrides; }
    public Map<String, String> getHeaders() { return headers; }
    public List<Extractor> getExtractors() { return extractors; }
    public Integer getExpectedStatus() { return expectedStatus; }
}

