package io.github.tony8864;

import io.github.tony8864.entity.ErrorStrategy;
import io.github.tony8864.entity.Scenario;
import io.github.tony8864.entity.Step;
import io.github.tony8864.ports.HttpExecutor;
import io.github.tony8864.ports.StepRunner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScenarioExecutorTest {
    @Test
    void shouldRunHooksAndStepsInOrder() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        StepRunner stepRunner = mock(StepRunner.class);
        ScenarioExecutor executor = new ScenarioExecutor(stepRunner);

        Scenario scenario = Scenario.of("Test Scenario")
                .before(() -> System.out.println("before"))
                .after(() -> System.out.println("after"));

        Step step1 = Step.of("Step 1", "EP1", "DATA1");
        Step step2 = Step.of("Step 2", "EP2", "DATA2");

        scenario.step(step1);
        scenario.step(step2);

        executor.run(scenario);

        verify(stepRunner).runStep(step1, scenario.getContext());
        verify(stepRunner).runStep(step2, scenario.getContext());
    }

    @Test
    void shouldRunBeforeAndAfterHooks() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        StepRunner stepRunner = mock(StepRunner.class);
        ScenarioExecutor executor = new ScenarioExecutor(stepRunner);

        final boolean[] flags = {false, false};

        Scenario scenario = Scenario.of("Hook Test")
                .before(() -> flags[0] = true)
                .after(() -> flags[1] = true);

        Step step = Step.of("Step 1", "EP1", "DATA1");
        scenario.step(step);

        executor.run(scenario);

        assertTrue(flags[0], "Before hook should run");
        assertTrue(flags[1], "After hook should run");
    }


    @Test
    void shouldAbortScenarioOnErrorByDefault() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        StepRunner stepRunner = mock(StepRunner.class);
        ScenarioExecutor executor = new ScenarioExecutor(stepRunner);

        Step step = Step.of("Failing Step", "EP1", "DATA1");
        Scenario scenario = Scenario.of("Abort by default").step(step);

        doThrow(new RuntimeException("boom"))
                .when(stepRunner).runStep(eq(step), any());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> executor.run(scenario));

        assertEquals("boom", ex.getMessage());
    }

    @Test
    void shouldAbortScenarioWhenErrorHandlerReturnsAbort() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        StepRunner stepRunner = mock(StepRunner.class);
        ScenarioExecutor executor = new ScenarioExecutor(stepRunner);

        Step step = Step.of("Step 1", "EP1", "DATA1");
        Scenario scenario = Scenario.of("Abort Test")
                .step(step)
                .onError(err -> ErrorStrategy.ABORT);

        doThrow(new RuntimeException("fail"))
                .when(stepRunner).runStep(eq(step), any());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> executor.run(scenario));

        assertEquals("fail", ex.getMessage());
    }

    @Test
    void shouldContinueWhenErrorHandlerReturnsContinue() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        StepRunner stepRunner = mock(StepRunner.class);
        ScenarioExecutor executor = new ScenarioExecutor(stepRunner);

        Step step1 = Step.of("Step 1", "EP1", "DATA1");
        Step step2 = Step.of("Step 2", "EP2", "DATA2");

        Scenario scenario = Scenario.of("Continue Test")
                .step(step1)
                .step(step2)
                .onError(err -> ErrorStrategy.CONTINUE);

        doThrow(new RuntimeException("fail"))
                .when(stepRunner).runStep(eq(step1), any());

        doNothing().when(stepRunner).runStep(eq(step2), any());

        executor.run(scenario);

        verify(stepRunner).runStep(eq(step2), any());
    }

    @Test
    void shouldRetryStepWhenErrorHandlerReturnsRetry() {
        HttpExecutor httpExecutor = mock(HttpExecutor.class);
        StepRunner stepRunner = mock(StepRunner.class);
        ScenarioExecutor executor = new ScenarioExecutor(stepRunner);

        Step step = Step.of("Retry Step", "EP1", "DATA1");
        Scenario scenario = Scenario.of("Retry Test")
                .step(step)
                .onError(err -> ErrorStrategy.RETRY);

        doThrow(new RuntimeException("first try fails"))
                .doNothing()
                .when(stepRunner).runStep(eq(step), any());

        executor.run(scenario);

        verify(stepRunner, times(2)).runStep(eq(step), any());
    }
}