package com.library.management.service;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.BookCreateRequest;
import com.library.management.dto.BookResponse;
import com.library.management.dto.BookUpdateRequest;
import com.library.management.entity.BookStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    BookResponse createBook(BookCreateRequest request);

    BookResponse getBookById(Long id);

    BookResponse getBookByIsbn(String isbn);

    PageResponse<BookResponse> getAllBooks(Pageable pageable);

    PageResponse<BookResponse> searchBooks(String search, Pageable pageable);

    List<BookResponse> getBooksByAuthor(Long authorId);

    List<BookResponse> getBooksByCategory(Long categoryId);

    List<BookResponse> getBooksByStatus(BookStatus status);

    BookResponse updateBook(Long id, BookUpdateRequest request);

    BookResponse updateBookStatus(Long id, BookStatus status);

    void deleteBook(Long id);

    boolean existsByIsbn(String isbn);

    long countByStatus(BookStatus status);
}