package com.library.management.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:9090/api";
    private static final int TIMEOUT_SECONDS = 30;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String username;
    private String password;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void clearCredentials() {
        this.username = null;
        this.password = null;
    }

    public boolean hasCredentials() {
        return username != null && password != null;
    }

    private String getAuthHeader() {
        if (username == null || password == null) {
            return null;
        }
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private HttpRequest.Builder createRequestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS));

        String authHeader = getAuthHeader();
        if (authHeader != null) {
            builder.header("Authorization", authHeader);
        }

        return builder;
    }

    public <T> CompletableFuture<T> get(String path, TypeReference<T> typeRef) {
        HttpRequest request = createRequestBuilder(path).GET().build();
        return sendAsync(request, typeRef);
    }

    public <T> CompletableFuture<T> post(String path, Object body, TypeReference<T> typeRef) {
        String jsonBody;
        try {
            jsonBody = body != null ? objectMapper.writeValueAsString(body) : "{}";
        } catch (IOException e) {
            return CompletableFuture.failedFuture(new ApiException(0, "Failed to serialize request", null, e));
        }

        HttpRequest request = createRequestBuilder(path)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendAsync(request, typeRef);
    }

    public <T> CompletableFuture<T> put(String path, Object body, TypeReference<T> typeRef) {
        String jsonBody;
        try {
            jsonBody = body != null ? objectMapper.writeValueAsString(body) : "{}";
        } catch (IOException e) {
            return CompletableFuture.failedFuture(new ApiException(0, "Failed to serialize request", null, e));
        }

        HttpRequest request = createRequestBuilder(path)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendAsync(request, typeRef);
    }

    public <T> CompletableFuture<T> patch(String path, Object body, TypeReference<T> typeRef) {
        String jsonBody;
        try {
            jsonBody = body != null ? objectMapper.writeValueAsString(body) : "{}";
        } catch (IOException e) {
            return CompletableFuture.failedFuture(new ApiException(0, "Failed to serialize request", null, e));
        }

        HttpRequest request = createRequestBuilder(path)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return sendAsync(request, typeRef);
    }

    public CompletableFuture<Void> delete(String path) {
        HttpRequest request = createRequestBuilder(path).DELETE().build();
        return sendAsync(request, new TypeReference<Void>() {}).thenApply(v -> null);
    }

    private <T> CompletableFuture<T> sendAsync(HttpRequest request, TypeReference<T> typeRef) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int statusCode = response.statusCode();
                    String body = response.body();

                    if (statusCode >= 200 && statusCode < 300) {
                        if (typeRef.getType().equals(Void.class)) {
                            return null;
                        }
                        try {
                            return objectMapper.readValue(body, typeRef);
                        } catch (IOException e) {
                            throw new ApiException(statusCode, "Failed to parse response: " + e.getMessage(), body, e);
                        }
                    } else {
                        throw new ApiException(statusCode, "HTTP " + statusCode, body);
                    }
                })
                .exceptionally(throwable -> {
                    if (throwable instanceof ApiException) {
                        throw (ApiException) throwable;
                    }
                    Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                    String message = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();

                    if (cause instanceof IOException || cause instanceof java.net.ConnectException) {
                        throw new ApiException(0, "Cannot connect to server. Please check if the backend is running.", null, cause);
                    } else if (cause instanceof java.util.concurrent.TimeoutException) {
                        throw new ApiException(0, "Connection timed out. Please try again.", null, cause);
                    } else {
                        throw new ApiException(0, "Request failed: " + message, null, cause);
                    }
                });
    }
}