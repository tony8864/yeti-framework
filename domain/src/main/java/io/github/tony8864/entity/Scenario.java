package io.github.tony8864.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Scenario {
    private final String name;
    private final List<Step> steps = new ArrayList<>();

    private Runnable beforeHook;
    private Runnable afterHook;
    private Function<StepError, ErrorStrategy> errorHandler;

    // Context of saved variables
    private final VariableContext context = new VariableContext();

    public Scenario(String name) {
        this.name = Objects.requireNonNull(name, "Scenario name must not be null");
    }

    public static Scenario of(String name) {
        return new Scenario(name);
    }

    // --- Hooks ---
    public Scenario before(Runnable hook) { this.beforeHook = hook; return this; }
    public Scenario after(Runnable hook) { this.afterHook = hook; return this; }
    public Scenario onError(Function<StepError, ErrorStrategy> handler) { this.errorHandler = handler; return this; }

    // --- Step adding ---
    public Step step(String description, String endpointName, String dataName) {
        Step step = Step.of(description, endpointName, dataName);
        this.steps.add(step);
        return step;
    }

    public Scenario step(Step step) {
        this.steps.add(step);
        return this;
    }

    public Scenario step(Step... steps) {
        this.steps.addAll(Arrays.asList(steps));
        return this;
    }

    // --- Getters ---
    public String getName() { return name; }
    public List<Step> getSteps() { return steps; }
    public Runnable getBeforeHook() { return beforeHook; }
    public Runnable getAfterHook() { return afterHook; }
    public Function<StepError, ErrorStrategy> getErrorHandler() { return errorHandler; }
    public VariableContext getContext() { return context; }
}
