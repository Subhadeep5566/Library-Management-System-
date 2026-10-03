package com.library.management.desktop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class AuthService {

    private static final String BASE_URL = "http://localhost:9090/api";
    private static final int TIMEOUT_SECONDS = 10;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String currentUsername;
    private String currentPassword;

    public AuthService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public CompletableFuture<AuthResult> authenticate(String username, String password) {
        this.currentUsername = username;
        this.currentPassword = password;

        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        String url = BASE_URL + "/users";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::processResponse)
                .exceptionally(throwable -> processException(throwable));
    }

    private AuthResult processResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String body = response.body();

        if (statusCode == 200) {
            return new AuthResult(true, "Authentication successful", null);
        } else if (statusCode == 401) {
            return new AuthResult(false, "Invalid username or password", null);
        } else if (statusCode == 403) {
            return new AuthResult(false, "Access denied", null);
        } else {
            return new AuthResult(false, "Server error: " + statusCode, null);
        }
    }

    private AuthResult processException(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null) {
            message = throwable.getClass().getSimpleName();
        }

        if (throwable instanceof IOException || throwable instanceof java.net.ConnectException) {
            return new AuthResult(false, "Cannot connect to server. Please check if the backend is running.", throwable);
        } else if (throwable instanceof java.util.concurrent.TimeoutException) {
            return new AuthResult(false, "Connection timed out. Please try again.", throwable);
        } else {
            return new AuthResult(false, "Authentication failed: " + message, throwable);
        }
    }

    public String getAuthHeader() {
        if (currentUsername == null || currentPassword == null) {
            return null;
        }
        String credentials = currentUsername + ":" + currentPassword;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public void clearCredentials() {
        this.currentUsername = null;
        this.currentPassword = null;
    }

    public boolean hasCredentials() {
        return currentUsername != null && currentPassword != null;
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final Throwable exception;

        public AuthResult(boolean success, String message, Throwable exception) {
            this.success = success;
            this.message = message;
            this.exception = exception;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getException() {
            return exception;
        }
    }
}