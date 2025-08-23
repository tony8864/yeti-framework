package io.github.tony8864.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariableContextTest {
    @Test
    void shouldPutAndGetVariable() {
        VariableContext ctx = new VariableContext();

        ctx.put("USER_ID", 42);

        assertEquals(42, ctx.get("USER_ID"));
    }

    @Test
    void shouldResolveSinglePlaceholder() {
        VariableContext ctx = new VariableContext();
        ctx.put("NAME", "Alice");

        String result = ctx.resolvePlaceholders("Hello ${NAME}");

        assertEquals("Hello Alice", result);
    }

    @Test
    void shouldResolveMultiplePlaceholders() {
        VariableContext ctx = new VariableContext();
        ctx.put("FIRST", "Alice");
        ctx.put("LAST", "Smith");

        String result = ctx.resolvePlaceholders("User: ${FIRST} ${LAST}");

        assertEquals("User: Alice Smith", result);
    }

    @Test
    void shouldResolveSameVariableMultipleTimes() {
        VariableContext ctx = new VariableContext();
        ctx.put("X", "42");

        String result = ctx.resolvePlaceholders("${X}-${X}-${X}");

        assertEquals("42-42-42", result);
    }

    @Test
    void shouldReturnInputUnchangedWhenNoPlaceholders() {
        VariableContext ctx = new VariableContext();

        String result = ctx.resolvePlaceholders("No variables here");

        assertEquals("No variables here", result);
    }

    @Test
    void shouldThrowWhenVariableNotFound() {
        VariableContext ctx = new VariableContext();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ctx.resolvePlaceholders("Hello ${MISSING}")
        );

        assertTrue(ex.getMessage().contains("Variablenot found: MISSING"));
    }
}