package io.github.tony8864.ports;

import io.github.tony8864.entity.Endpoint;
import io.github.tony8864.entity.Response;

import java.util.Map;

public interface HttpExecutor {
    Response execute(Endpoint endpoint,
                     Object body,
                     Map<String, String> pathParams,
                     Map<String, String> queryParams,
                     Map<String, String> headers);
}
