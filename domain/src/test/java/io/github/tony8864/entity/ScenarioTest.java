package io.github.tony8864.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioTest {
    @Test
    void shouldCreateScenarioWithName() {
        Scenario scenario = Scenario.of("My Scenario");

        assertEquals("My Scenario", scenario.getName());
        assertTrue(scenario.getSteps().isEmpty());
        assertNotNull(scenario.getContext());
    }

    @Test
    void shouldAddStepsInOrder() {
        Scenario scenario = Scenario.of("Order Test");

        Step step1 = Step.of("Step 1", "EP1", "DATA1");
        Step step2 = Step.of("Step 2", "EP2", "DATA2");

        scenario.step(step1).step(step2);

        List<Step> steps = scenario.getSteps();
        assertEquals(2, steps.size());
        assertEquals("Step 1", steps.get(0).getDescription());
        assertEquals("Step 2", steps.get(1).getDescription());
    }

    @Test
    void shouldAllowInlineStepDefinition() {
        Scenario scenario = Scenario.of("Inline Step Test");

        Step step = scenario.step("Step 1", "EP1", "DATA1");

        assertEquals(1, scenario.getSteps().size());
        assertEquals(step, scenario.getSteps().get(0));
        assertEquals("EP1", step.getEndpointName());
        assertEquals("DATA1", step.getDataName());
    }

    @Test
    void shouldStoreBeforeAndAfterHooks() {
        Scenario scenario = Scenario.of("Hook Test");

        Runnable before = () -> System.out.println("Before");
        Runnable after = () -> System.out.println("After");

        scenario.before(before).after(after);

        assertEquals(before, scenario.getBeforeHook());
        assertEquals(after, scenario.getAfterHook());
    }

    @Test
    void shouldStoreErrorHandler() {
        Scenario scenario = Scenario.of("Error Test");

        scenario.onError(err -> ErrorStrategy.CONTINUE);

        assertNotNull(scenario.getErrorHandler());
        assertEquals(ErrorStrategy.CONTINUE,
                scenario.getErrorHandler().apply(new StepError(Step.of("S", "EP", "DATA"), new RuntimeException())));
    }

    @Test
    void shouldHaveSeparateVariableContextsPerScenario() {
        Scenario s1 = Scenario.of("S1");
        Scenario s2 = Scenario.of("S2");

        assertNotSame(s1.getContext(), s2.getContext());
    }
}