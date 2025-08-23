package io.github.tony8864.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataRegistryTest {

    record UserRegistrationDto(String name, String pass) {}

    @BeforeEach
    void clearRegistry() {
        DataRegistry.clear();
    }

    @Test
    void shouldRegisterAndRetrieveData() {
        UserRegistrationDto alice = new UserRegistrationDto("alice", "pw123");
        DataRegistry.register("ALICE", alice);

        DataRef result = DataRegistry.get("ALICE");

        assertNotNull(result);
        assertEquals("ALICE", result.name());
        assertEquals(alice, result.value());
    }

    @Test
    void shouldThrowWhenDataNotRegistered() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> DataRegistry.get("UNKNOWN")
        );
        assertTrue(ex.getMessage().contains("No data registered with name: UNKNOWN"));
    }

    @Test
    void shouldOverrideDataIfRegisteredWithSameName() {
        DataRegistry.register("ALICE", "old");
        DataRegistry.register("ALICE", "new");

        DataRef result = DataRegistry.get("ALICE");

        assertEquals("new", result.value());
    }
}