package io.github.tony8864.entity;

import java.util.concurrent.TimeoutException;

public record StepError(Step step, Throwable cause) {
    public boolean isHttpTimeout() {
        return cause instanceof TimeoutException;
    }
}
