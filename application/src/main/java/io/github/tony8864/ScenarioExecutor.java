package io.github.tony8864;

import io.github.tony8864.entity.*;
import io.github.tony8864.ports.StepRunner;

public class ScenarioExecutor {
    private final StepRunner stepRunner;

    public ScenarioExecutor(StepRunner stepRunner) {
        this.stepRunner = stepRunner;
    }

    public void run(Scenario scenario) {
        VariableContext context = scenario.getContext();
        if (scenario.getBeforeHook() != null) scenario.getBeforeHook().run();

        for (Step step : scenario.getSteps()) {
            executeStepWithHandling(step, context, scenario);
        }

        if (scenario.getAfterHook() != null) scenario.getAfterHook().run();
    }

    private void executeStepWithHandling(Step step, VariableContext context, Scenario scenario) {
        try {
            stepRunner.runStep(step, context);
        } catch (RuntimeException ex) {
            handleStepError(step, context, scenario, ex);
        }
    }

    private void handleStepError(Step step, VariableContext context, Scenario scenario, RuntimeException ex) {
        var handler = scenario.getErrorHandler();

        if (handler != null) {
            ErrorStrategy strategy = handler.apply(new StepError(step, ex));
            switch (strategy) {
                case CONTINUE -> {}
                case RETRY -> stepRunner.runStep(step, context);
                case ABORT -> throw ex;
            }
        }
        else {
            throw ex;
        }
    }
}
