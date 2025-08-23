package io.github.tony8864.entity;

import com.jayway.jsonpath.JsonPath;

public record Extractor(String variableName, String expression) {
    public void extract(VariableContext context, Response response) {
        Object value = JsonPath.read(response.body(), expression);
        context.put(variableName, value);
    }
}
