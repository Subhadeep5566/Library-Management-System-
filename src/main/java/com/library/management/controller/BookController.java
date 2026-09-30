package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.BookCreateRequest;
import com.library.management.dto.BookResponse;
import com.library.management.dto.BookUpdateRequest;
import com.library.management.entity.BookStatus;
import com.library.management.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.ok(ApiResponse.success("Book created successfully", book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookByIsbn(@PathVariable String isbn) {
        BookResponse book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(ApiResponse.success(book));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<BookResponse> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> searchBooks(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookResponse> books = bookService.searchBooks(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksByAuthor(@PathVariable Long authorId) {
        List<BookResponse> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksByCategory(@PathVariable Long categoryId) {
        List<BookResponse> books = bookService.getBooksByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksByStatus(@PathVariable BookStatus status) {
        List<BookResponse> books = bookService.getBooksByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest request) {
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success("Book updated successfully", book));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BookResponse>> updateBookStatus(
            @PathVariable Long id,
            @RequestParam BookStatus status) {
        BookResponse book = bookService.updateBookStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Book status updated successfully", book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully"));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countByStatus(@PathVariable BookStatus status) {
        long count = bookService.countByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}