package io.github.tony8864.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndpointRegistryTest {
    @BeforeEach
    void clearRegistry() {
        EndpointRegistry.clear();
    }

    @Test
    void shouldRegisterAndRetrieveEndpoint() {
        Endpoint endpoint = Endpoint.of(
                "REGISTER_USER",
                HttpMethod.POST,
                "/users/register",
                String.class,
                String.class
        );

        EndpointRegistry.register(endpoint);

        Endpoint result = EndpointRegistry.get("REGISTER_USER");

        assertNotNull(result);
        assertEquals("REGISTER_USER", result.name()); // record accessor
        assertEquals(HttpMethod.POST, result.method());
        assertEquals("/users/register", result.urlTemplate());
    }

    @Test
    void shouldThrowWhenEndpointNotRegistered() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> EndpointRegistry.get("UNKNOWN")
        );

        assertTrue(ex.getMessage().contains("No endpoint registered with name: UNKNOWN"));
    }

    @Test
    void shouldOverrideEndpointIfRegisteredWithSameName() {
        Endpoint oldEndpoint = Endpoint.of(
                "REGISTER_USER",
                HttpMethod.POST,
                "/users/register/old",
                String.class,
                String.class
        );

        Endpoint newEndpoint = Endpoint.of(
                "REGISTER_USER",
                HttpMethod.POST,
                "/users/register/new",
                String.class,
                String.class
        );

        EndpointRegistry.register(oldEndpoint);
        EndpointRegistry.register(newEndpoint);

        Endpoint result = EndpointRegistry.get("REGISTER_USER");

        assertEquals("/users/register/new", result.urlTemplate());
    }
}