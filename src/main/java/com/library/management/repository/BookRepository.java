package com.library.management.repository;

import com.library.management.entity.Book;
import com.library.management.entity.BookStatus;
import com.library.management.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByAuthorId(Long authorId);

    List<Book> findByCategoryId(Long categoryId);

    List<Book> findByStatus(BookStatus status);

    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthor_NameContainingIgnoreCase(String authorName);

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:search% OR b.isbn LIKE %:search% OR b.author.name LIKE %:search%")
    Page<Book> searchBooks(@Param("search") String search, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.category = :category AND b.status = :status")
    Page<Book> findByCategoryAndStatus(@Param("category") Category category, @Param("status") BookStatus status, Pageable pageable);

    boolean existsByIsbn(String isbn);

    long countByStatus(BookStatus status);
}