package io.github.tony8864.entity;

import java.util.HashMap;
import java.util.Map;

public record Response(int statusCode, String body, Map<String, String> headers) {
    public Response(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }
}
