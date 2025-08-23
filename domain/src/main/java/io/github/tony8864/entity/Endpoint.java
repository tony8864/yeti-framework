package io.github.tony8864.entity;

public record Endpoint(
        String name,
        String urlTemplate,
        HttpMethod method,
        Class<?> requestType,
        Class<?> responseType
) {
    public static Endpoint of(String name, HttpMethod method, String urlTemplate, Class<?> requestType, Class<?> responseType) {
        return new Endpoint(name, urlTemplate, method, requestType, responseType);
    }
}
