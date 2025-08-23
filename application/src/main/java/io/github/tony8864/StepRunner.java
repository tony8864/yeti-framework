package io.github.tony8864;

import io.github.tony8864.entity.Step;
import io.github.tony8864.entity.VariableContext;

public interface StepRunner {
    void runStep(Step step, VariableContext context);
}
