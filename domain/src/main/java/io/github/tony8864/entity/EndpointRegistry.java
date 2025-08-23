package io.github.tony8864.entity;

import java.util.HashMap;
import java.util.Map;

public class EndpointRegistry {
    private static final Map<String, Endpoint> endpoints = new HashMap<>();

    public static void register(Endpoint endpoint) {
        endpoints.put(endpoint.name(), endpoint);
    }

    public static Endpoint get(String name) {
        Endpoint endpoint = endpoints.get(name);
        if (endpoint == null) {
            throw new IllegalArgumentException("No endpoint registered with name: " + name);
        }
        return endpoint;
    }

    public static void clear() {
        endpoints.clear();
    }
}
