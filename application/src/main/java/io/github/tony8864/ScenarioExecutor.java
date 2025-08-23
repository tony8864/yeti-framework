package io.github.tony8864;

import io.github.tony8864.entity.*;
import io.github.tony8864.ports.HttpExecutor;

public class ScenarioExecutor {
    private final HttpExecutor httpExecutor;
    private final StepRunner stepRunner;

    public ScenarioExecutor(HttpExecutor httpExecutor, StepRunner stepRunner) {
        this.httpExecutor = httpExecutor;
        this.stepRunner = stepRunner;
    }

    public void run(Scenario scenario) {
        VariableContext context = scenario.getContext();
        if (scenario.getBeforeHook() != null) scenario.getBeforeHook().run();

        for (Step step : scenario.getSteps()) {
            try {
                stepRunner.runStep(step, context);
            } catch (Exception ex) {
                if (scenario.getErrorHandler() != null) {
                    ErrorStrategy strategy = scenario.getErrorHandler().apply(new StepError(step, ex));
                    switch (strategy) {
                        case CONTINUE -> {
                            continue;
                        }
                        case RETRY -> {
                            stepRunner.runStep(step, context);
                            continue;
                        }
                        case ABORT -> {
                            throw ex;
                        }
                    }
                }
                else {
                    throw ex;
                }
            }
        }

        if (scenario.getAfterHook() != null) scenario.getAfterHook().run();
    }
}
