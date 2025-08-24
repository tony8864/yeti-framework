package io.github.tony8864;

import io.github.tony8864.entity.Endpoint;
import io.github.tony8864.entity.HttpMethod;
import io.github.tony8864.entity.Response;
import io.github.tony8864.ports.HttpExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

public class WebClientHttpExecutor implements HttpExecutor {

    private final WebClient webClient;

    public WebClientHttpExecutor(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Response execute(Endpoint endpoint, Object body, Map<String, String> pathParams, Map<String, String> queryParams, Map<String, String> headers) {
        WebClient.RequestBodySpec requestBodySpec =
                webClient.method(toSpring(endpoint.method()))
                        .uri(uriBuilder -> {
                            String url = endpoint.urlTemplate();
                            for (var entry : pathParams.entrySet()) {
                                url = url.replace("{" + entry.getKey() + "}", entry.getValue());
                            }
                            uriBuilder.path(url);
                            queryParams.forEach(uriBuilder::queryParam);
                            return uriBuilder.build();
                        });
        headers.forEach(requestBodySpec::header);

        if (body != null && supportsBody(endpoint.method())) {
            requestBodySpec = (WebClient.RequestBodySpec) requestBodySpec.bodyValue(body);
        }

        ResponseEntity<String> entity = requestBodySpec
                .exchangeToMono(response -> response.toEntity(String.class))
                .block();

        Objects.requireNonNull(entity, "ResponseEntity must not be null");

        return new Response(entity.getStatusCode().value(), entity.getBody(), entity.getHeaders().toSingleValueMap());
    }

    private boolean supportsBody(HttpMethod method) {
        return switch (method) {
            case POST, PUT, PATCH -> true;
            default -> false;
        };
    }

    private org.springframework.http.HttpMethod toSpring(HttpMethod method) {
        return org.springframework.http.HttpMethod.valueOf(method.name());
    }
}
