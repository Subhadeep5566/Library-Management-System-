package com.library.management.service.impl;

import com.library.management.dto.AuthorResponse;
import com.library.management.dto.AuthorCreateRequest;
import com.library.management.dto.PageResponse;
import com.library.management.entity.Author;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.repository.AuthorRepository;
import com.library.management.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public AuthorResponse createAuthor(AuthorCreateRequest request) {
        if (authorRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Author already exists with name: " + request.getName());
        }

        Author author = Author.builder()
                .name(request.getName())
                .biography(request.getBiography())
                .birthDate(request.getBirthDate())
                .deathDate(request.getDeathDate())
                .nationality(request.getNationality())
                .build();

        Author savedAuthor = authorRepository.save(author);
        return mapToResponse(savedAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return mapToResponse(author);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuthorResponse> getAllAuthors(Pageable pageable) {
        Page<Author> authors = authorRepository.findAll(pageable);
        return mapToPageResponse(authors);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuthorResponse> searchAuthors(String search, Pageable pageable) {
        Page<Author> authors = authorRepository.searchAuthors(search, pageable);
        return mapToPageResponse(authors);
    }

    @Override
    public AuthorResponse updateAuthor(Long id, AuthorCreateRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        if (request.getName() != null && !request.getName().equals(author.getName())) {
            if (authorRepository.existsByName(request.getName())) {
                throw new DuplicateResourceException("Author already exists with name: " + request.getName());
            }
            author.setName(request.getName());
        }
        if (request.getBiography() != null) author.setBiography(request.getBiography());
        if (request.getBirthDate() != null) author.setBirthDate(request.getBirthDate());
        if (request.getDeathDate() != null) author.setDeathDate(request.getDeathDate());
        if (request.getNationality() != null) author.setNationality(request.getNationality());

        Author savedAuthor = authorRepository.save(author);
        return mapToResponse(savedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return authorRepository.existsByName(name);
    }

    private AuthorResponse mapToResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .biography(author.getBiography())
                .birthDate(author.getBirthDate())
                .deathDate(author.getDeathDate())
                .nationality(author.getNationality())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .bookCount(author.getBooks() != null ? author.getBooks().size() : 0)
                .build();
    }

    private PageResponse<AuthorResponse> mapToPageResponse(Page<Author> authors) {
        return PageResponse.<AuthorResponse>builder()
                .content(authors.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(authors.getNumber())
                .pageSize(authors.getSize())
                .totalElements(authors.getTotalElements())
                .totalPages(authors.getTotalPages())
                .first(authors.isFirst())
                .last(authors.isLast())
                .empty(authors.isEmpty())
                .build();
    }
}