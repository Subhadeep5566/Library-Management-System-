package com.library.management.desktop.service;

import com.library.management.desktop.dto.ApiResponse;
import com.library.management.desktop.dto.BookResponse;
import com.library.management.desktop.dto.AuthorResponse;
import com.library.management.desktop.dto.CategoryResponse;
import com.library.management.desktop.dto.UserResponse;
import com.library.management.desktop.dto.BorrowResponse;
import com.library.management.desktop.dto.ReturnResponse;
import com.library.management.desktop.dto.FineResponse;
import com.library.management.desktop.dto.PageResponse;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.concurrent.CompletableFuture;

public class ApiService {

    private final ApiClient apiClient;

    public ApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // ==================== BOOKS ====================

    public CompletableFuture<PageResponse<BookResponse>> getBooks(int page, int size, String sortBy, String sortDir) {
        String path = String.format("/books?page=%d&size=%d&sortBy=%s&sortDir=%s", page, size, sortBy, sortDir);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<BookResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<PageResponse<BookResponse>> searchBooks(String query, int page, int size) {
        String path = String.format("/books/search?q=%s&page=%d&size=%d", query, page, size);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<BookResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<BookResponse> getBookById(Long id) {
        return apiClient.get("/books/" + id, new TypeReference<ApiResponse<BookResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<BookResponse> createBook(Object request) {
        return apiClient.post("/books", request, new TypeReference<ApiResponse<BookResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<BookResponse> updateBook(Long id, Object request) {
        return apiClient.put("/books/" + id, request, new TypeReference<ApiResponse<BookResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<Void> deleteBook(Long id) {
        return apiClient.delete("/books/" + id);
    }

    // ==================== AUTHORS ====================

    public CompletableFuture<PageResponse<AuthorResponse>> getAuthors(int page, int size, String sortBy, String sortDir) {
        String path = String.format("/authors?page=%d&size=%d&sortBy=%s&sortDir=%s", page, size, sortBy, sortDir);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<AuthorResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<PageResponse<AuthorResponse>> searchAuthors(String query, int page, int size) {
        String path = String.format("/authors/search?q=%s&page=%d&size=%d", query, page, size);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<AuthorResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<AuthorResponse> getAuthorById(Long id) {
        return apiClient.get("/authors/" + id, new TypeReference<ApiResponse<AuthorResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<AuthorResponse> createAuthor(Object request) {
        return apiClient.post("/authors", request, new TypeReference<ApiResponse<AuthorResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<AuthorResponse> updateAuthor(Long id, Object request) {
        return apiClient.put("/authors/" + id, request, new TypeReference<ApiResponse<AuthorResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<Void> deleteAuthor(Long id) {
        return apiClient.delete("/authors/" + id);
    }

    // ==================== CATEGORIES ====================

    public CompletableFuture<PageResponse<CategoryResponse>> getCategories(int page, int size, String sortBy, String sortDir) {
        String path = String.format("/categories?page=%d&size=%d&sortBy=%s&sortDir=%s", page, size, sortBy, sortDir);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<CategoryResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<PageResponse<CategoryResponse>> searchCategories(String query, int page, int size) {
        String path = String.format("/categories/search?q=%s&page=%d&size=%d", query, page, size);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<CategoryResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<CategoryResponse> getCategoryById(Long id) {
        return apiClient.get("/categories/" + id, new TypeReference<ApiResponse<CategoryResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<CategoryResponse> createCategory(Object request) {
        return apiClient.post("/categories", request, new TypeReference<ApiResponse<CategoryResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<CategoryResponse> updateCategory(Long id, Object request) {
        return apiClient.put("/categories/" + id, request, new TypeReference<ApiResponse<CategoryResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<Void> deleteCategory(Long id) {
        return apiClient.delete("/categories/" + id);
    }

    // ==================== USERS ====================

    public CompletableFuture<PageResponse<UserResponse>> getUsers(int page, int size, String sortBy, String sortDir) {
        String path = String.format("/users?page=%d&size=%d&sortBy=%s&sortDir=%s", page, size, sortBy, sortDir);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<UserResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<PageResponse<UserResponse>> searchUsers(String query, int page, int size) {
        String path = String.format("/users/search?q=%s&page=%d&size=%d", query, page, size);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<UserResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<UserResponse> getUserById(Long id) {
        return apiClient.get("/users/" + id, new TypeReference<ApiResponse<UserResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<UserResponse> createUser(Object request) {
        return apiClient.post("/users", request, new TypeReference<ApiResponse<UserResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<UserResponse> updateUser(Long id, Object request) {
        return apiClient.put("/users/" + id, request, new TypeReference<ApiResponse<UserResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<UserResponse> updateUserStatus(Long id, String status) {
        return apiClient.patch("/users/" + id + "/status?status=" + status, null, new TypeReference<ApiResponse<UserResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<UserResponse> updateUserRole(Long id, String role) {
        return apiClient.patch("/users/" + id + "/role?role=" + role, null, new TypeReference<ApiResponse<UserResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<Void> deleteUser(Long id) {
        return apiClient.delete("/users/" + id);
    }

    // ==================== BORROWS ====================

    public CompletableFuture<PageResponse<BorrowResponse>> getBorrows(int page, int size, String sortBy, String sortDir) {
        String path = String.format("/borrows?page=%d&size=%d&sortBy=%s&sortDir=%s", page, size, sortBy, sortDir);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<BorrowResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<PageResponse<BorrowResponse>> searchBorrows(String query, int page, int size) {
        String path = String.format("/borrows/search?q=%s&page=%d&size=%d", query, page, size);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<BorrowResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<BorrowResponse> getBorrowById(Long id) {
        return apiClient.get("/borrows/" + id, new TypeReference<ApiResponse<BorrowResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<BorrowResponse> createBorrow(Object request) {
        return apiClient.post("/borrows", request, new TypeReference<ApiResponse<BorrowResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<Void> returnBook(Long id) {
        return apiClient.post("/borrows/" + id + "/return", null, new TypeReference<ApiResponse<Void>>() {})
                .thenApply(response -> null);
    }

    public CompletableFuture<Void> markLost(Long id) {
        return apiClient.post("/borrows/" + id + "/lost", null, new TypeReference<ApiResponse<Void>>() {})
                .thenApply(response -> null);
    }

    // ==================== RETURNS ====================

    public CompletableFuture<PageResponse<ReturnResponse>> getReturns(int page, int size, String sortBy, String sortDir) {
        String path = String.format("/returns?page=%d&size=%d&sortBy=%s&sortDir=%s", page, size, sortBy, sortDir);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<ReturnResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<PageResponse<ReturnResponse>> searchReturns(String query, int page, int size) {
        String path = String.format("/returns/search?q=%s&page=%d&size=%d", query, page, size);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<ReturnResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<ReturnResponse> getReturnById(Long id) {
        return apiClient.get("/returns/" + id, new TypeReference<ApiResponse<ReturnResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<ReturnResponse> createReturn(Object request) {
        return apiClient.post("/returns", request, new TypeReference<ApiResponse<ReturnResponse>>() {})
                .thenApply(response -> response.getData());
    }

    // ==================== FINES ====================

    public CompletableFuture<PageResponse<FineResponse>> getFines(int page, int size, String sortBy, String sortDir) {
        String path = String.format("/fines?page=%d&size=%d&sortBy=%s&sortDir=%s", page, size, sortBy, sortDir);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<FineResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<PageResponse<FineResponse>> searchFines(String query, int page, int size) {
        String path = String.format("/fines/search?q=%s&page=%d&size=%d", query, page, size);
        return apiClient.get(path, new TypeReference<ApiResponse<PageResponse<FineResponse>>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<FineResponse> getFineById(Long id) {
        return apiClient.get("/fines/" + id, new TypeReference<ApiResponse<FineResponse>>() {})
                .thenApply(response -> response.getData());
    }

    public CompletableFuture<Void> payFine(Long id) {
        return apiClient.post("/fines/" + id + "/pay", null, new TypeReference<ApiResponse<Void>>() {})
                .thenApply(response -> null);
    }

    public CompletableFuture<Void> waiveFine(Long id) {
        return apiClient.post("/fines/" + id + "/waive", null, new TypeReference<ApiResponse<Void>>() {})
                .thenApply(response -> null);
    }

    public CompletableFuture<Void> generateFines() {
        return apiClient.post("/fines/generate", null, new TypeReference<ApiResponse<Void>>() {})
                .thenApply(response -> null);
    }
}