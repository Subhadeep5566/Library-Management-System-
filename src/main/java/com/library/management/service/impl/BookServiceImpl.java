package com.library.management.service.impl;

import com.library.management.dto.BookResponse;
import com.library.management.dto.BookCreateRequest;
import com.library.management.dto.BookUpdateRequest;
import com.library.management.dto.PageResponse;
import com.library.management.entity.Book;
import com.library.management.entity.BookStatus;
import com.library.management.entity.Author;
import com.library.management.entity.Category;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.exception.BusinessLogicException;
import com.library.management.repository.BookRepository;
import com.library.management.repository.AuthorRepository;
import com.library.management.repository.CategoryRepository;
import com.library.management.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public BookResponse createBook(BookCreateRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("Book with ISBN already exists: " + request.getIsbn());
        }

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .description(request.getDescription())
                .publicationYear(request.getPublicationYear())
                .publisher(request.getPublisher())
                .language(request.getLanguage())
                .pageCount(request.getPageCount())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .shelfLocation(request.getShelfLocation())
                .price(request.getPrice())
                .author(author)
                .category(category)
                .status(BookStatus.AVAILABLE)
                .build();

        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
        return mapToResponse(book);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return mapToPageResponse(books);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooks(String search, Pageable pageable) {
        Page<Book> books = bookRepository.searchBooks(search, pageable);
        return mapToPageResponse(books);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByCategory(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByStatus(BookStatus status) {
        return bookRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getDescription() != null) book.setDescription(request.getDescription());
        if (request.getPublicationYear() != null) book.setPublicationYear(request.getPublicationYear());
        if (request.getPublisher() != null) book.setPublisher(request.getPublisher());
        if (request.getLanguage() != null) book.setLanguage(request.getLanguage());
        if (request.getPageCount() != null) book.setPageCount(request.getPageCount());
        if (request.getTotalCopies() != null) {
            int diff = request.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(request.getTotalCopies());
            book.setAvailableCopies(book.getAvailableCopies() + diff);
        }
        if (request.getShelfLocation() != null) book.setShelfLocation(request.getShelfLocation());
        if (request.getPrice() != null) book.setPrice(request.getPrice());
        if (request.getAuthorId() != null) {
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));
            book.setAuthor(author);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            book.setCategory(category);
        }

        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    @Override
    public BookResponse updateBookStatus(Long id, BookStatus status) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setStatus(status);
        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(BookStatus status) {
        return bookRepository.countByStatus(status);
    }

    private BookResponse mapToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .description(book.getDescription())
                .publicationYear(book.getPublicationYear())
                .publisher(book.getPublisher())
                .language(book.getLanguage())
                .pageCount(book.getPageCount())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .shelfLocation(book.getShelfLocation())
                .price(book.getPrice())
                .status(book.getStatus())
                .authorId(book.getAuthor().getId())
                .authorName(book.getAuthor().getName())
                .categoryId(book.getCategory().getId())
                .categoryName(book.getCategory().getName())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    private PageResponse<BookResponse> mapToPageResponse(Page<Book> books) {
        return PageResponse.<BookResponse>builder()
                .content(books.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(books.getNumber())
                .pageSize(books.getSize())
                .totalElements(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .first(books.isFirst())
                .last(books.isLast())
                .empty(books.isEmpty())
                .build();
    }
}