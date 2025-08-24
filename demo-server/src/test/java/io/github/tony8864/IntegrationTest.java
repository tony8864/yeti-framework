package io.github.tony8864;

import io.github.tony8864.entity.*;
import io.github.tony8864.ports.StepRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = DemoServerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
public class IntegrationTest {
    @LocalServerPort
    private int port;

    @Test
    void shouldCreateAndFetch() {
        EndpointRegistry.clear();
        EndpointRegistry.register(Endpoint.of("CREATE_USER", HttpMethod.POST, "/users", UserRequest.class, String.class));
        EndpointRegistry.register(Endpoint.of("GET_USER", HttpMethod.GET, "/users/{id}", null, String.class));

        DataRegistry.clear();
        DataRegistry.register("CHARLIE", new UserRequest("Charlie"));

        Scenario scenario = Scenario.of("Create and fetch user");

        scenario.step("Create Charlie", "CREATE_USER", "CHARLIE")
                .saveAs("userId", "$.id")
                .saveAs("userName", "$.name")
                .expectStatus(201);

        scenario.step("Get Charlie by id", "GET_USER", null)
                .withPathParam("id", "${userId}")
                .saveAs("fetchedName", "$.name")
                .expectStatus(200);

        String baseUrl = "http://localhost:" + port;
        StepRunner runner = new HttpStepRunner(new WebClientHttpExecutor(baseUrl));
        ScenarioExecutor executor = new ScenarioExecutor(runner);

        executor.run(scenario);

        assertEquals("Charlie", scenario.getContext().get("userName"));
        assertEquals("Charlie", scenario.getContext().get("fetchedName"));
    }

    @Test
    void shouldUpdateUser() {
        EndpointRegistry.clear();
        EndpointRegistry.register(Endpoint.of("CREATE_USER", HttpMethod.POST, "/users", UserRequest.class, String.class));
        EndpointRegistry.register(Endpoint.of(
                "UPDATE_USER", HttpMethod.PUT, "/users/{id}", UserRequest.class, String.class));
        EndpointRegistry.register(Endpoint.of(
                "GET_USER", HttpMethod.GET, "/users/{id}", null, String.class));

        DataRegistry.clear();
        DataRegistry.register("ALICE_USER", new UserRequest("Alice"));
        DataRegistry.register("CHARLIE_USER", new UserRequest("Charlie"));

        Scenario scenario = Scenario.of("Update user");

        Step createStep = Step.of("Create Alice", "CREATE_USER", "ALICE_USER")
                .saveAs("userId", "$.id")
                .saveAs("userName", "$.name")
                .expectStatus(201);

        Step updateStep = Step.of("Update Alice to Charlie", "UPDATE_USER", "CHARLIE_USER")
                .withPathParam("id", "${userId}")
                .saveAs("updated_name", "$.name")
                .expectStatus(200);

        Step fetchStep = Step.of("Fetch updated user", "GET_USER", null)
                .withPathParam("id", "${userId}")
                .saveAs("fetched_name", "$.name")
                .expectStatus(200);

        scenario.step(createStep, updateStep, fetchStep);

        String baseUrl = "http://localhost:" + port;
        StepRunner runner = new HttpStepRunner(new WebClientHttpExecutor(baseUrl));
        ScenarioExecutor executor = new ScenarioExecutor(runner);

        executor.run(scenario);

        assertEquals("Charlie", scenario.getContext().get("updated_name"));
        assertEquals("Charlie", scenario.getContext().get("fetched_name"));
    }

    @Test
    void shouldDeleteUser() {
        EndpointRegistry.clear();
        EndpointRegistry.register(Endpoint.of(
                "CREATE_USER", HttpMethod.POST, "/users", UserRequest.class, String.class));
        EndpointRegistry.register(Endpoint.of(
                "DELETE_USER", HttpMethod.DELETE, "/users/{id}", null, String.class));
        EndpointRegistry.register(Endpoint.of(
                "GET_USER", HttpMethod.GET, "/users/{id}", null, String.class));

        DataRegistry.clear();
        DataRegistry.register("BOB_USER", new UserRequest("Bob"));

        Scenario scenario = Scenario.of("Delete user");

        Step createStep = Step.of("Create Bob", "CREATE_USER", "BOB_USER")
                .saveAs("bobId", "$.id")
                .expectStatus(201);

        Step deleteStep = Step.of("Delete Bob", "DELETE_USER", null)
                .withPathParam("id", "${bobId}") // resolve to created id
                .expectStatus(204);

        Step fetchStep = Step.of("Fetch deleted user", "GET_USER", null)
                .withPathParam("id", "${bobId}")
                .expectStatus(404);

        scenario.step(createStep, deleteStep, fetchStep);

        String baseUrl = "http://localhost:" + port;
        StepRunner runner = new HttpStepRunner(new WebClientHttpExecutor(baseUrl));
        ScenarioExecutor executor = new ScenarioExecutor(runner);

        executor.run(scenario);
    }

    record UserRequest(String name) {}
}
