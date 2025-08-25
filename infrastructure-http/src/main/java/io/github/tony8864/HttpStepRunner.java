package io.github.tony8864;

import io.github.tony8864.entity.*;
import io.github.tony8864.ports.HttpExecutor;
import io.github.tony8864.ports.StepRunner;

import java.util.HashMap;
import java.util.Map;

public class HttpStepRunner implements StepRunner {

    private final HttpExecutor httpExecutor;

    public HttpStepRunner(HttpExecutor httpExecutor) {
        this.httpExecutor = httpExecutor;
    }

    @Override
    public void runStep(Step step, VariableContext context) {
        Endpoint endpoint = EndpointRegistry.get(step.getEndpointName());

        Object requestData = null;
        if (step.getDataName() != null) {
            requestData = DataRegistry.get(step.getDataName()).value();
        }

        Map<String, String> resolvedPathParams = resolveMap(step.getPathParams(), context);
        Map<String, String> resolvedQueryParams = resolveMap(step.getQueryParams(), context);
        Map<String, String> resolvedHeaders = resolveMap(step.getHeaders(), context);

        Response response = httpExecutor.execute(
                endpoint,
                requestData,
                resolvedPathParams,
                resolvedQueryParams,
                resolvedHeaders
        );

        if (step.getExpectedStatus() != null && response.statusCode() != step.getExpectedStatus()) {
            throw new AssertionError("Expected " + step.getExpectedStatus() + " but got " + response.statusCode());
        }

        step.getExtractors().forEach(e -> e.extract(context, response));
    }

    private Map<String, String> resolveMap(Map<String, String> input, VariableContext context) {
        Map<String, String> resolved = new HashMap<>();
        input.forEach((k, v) -> resolved.put(k, context.resolvePlaceholders(v)));
        return resolved;
    }
}
