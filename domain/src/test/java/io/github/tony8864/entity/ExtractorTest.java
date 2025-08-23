package io.github.tony8864.entity;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtractorTest {
    @Test
    void shouldExtractValueFromJsonAndSaveToContext() {
        String json = """
            {
              "id": 123,
              "user": { "name": "Alice" }
            }
            """;
        Response response = new Response(200, json, Map.of());
        VariableContext ctx = new VariableContext();

        Extractor extractor = new Extractor("USER_NAME", "$.user.name");

        extractor.extract(ctx, response);

        assertEquals("Alice", ctx.get("USER_NAME"));
    }
}