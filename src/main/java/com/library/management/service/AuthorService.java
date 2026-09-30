package com.library.management.service;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.AuthorCreateRequest;
import com.library.management.dto.AuthorResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {

    AuthorResponse createAuthor(AuthorCreateRequest request);

    AuthorResponse getAuthorById(Long id);

    PageResponse<AuthorResponse> getAllAuthors(Pageable pageable);

    PageResponse<AuthorResponse> searchAuthors(String search, Pageable pageable);

    AuthorResponse updateAuthor(Long id, AuthorCreateRequest request);

    void deleteAuthor(Long id);

    boolean existsByName(String name);
}