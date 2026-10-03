package com.library.management.repository;

import com.library.management.entity.Borrow;
import com.library.management.entity.BorrowStatus;
import com.library.management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUserId(Long userId);

    List<Borrow> findByUserIdAndStatus(Long userId, BorrowStatus status);

    Page<Borrow> findByUserId(Long userId, Pageable pageable);

    List<Borrow> findByBookId(Long bookId);

    List<Borrow> findByStatus(BorrowStatus status);

    @Query("SELECT b FROM Borrow b WHERE b.status = :status AND b.dueDate < :today")
    List<Borrow> findOverdueBorrows(@Param("status") BorrowStatus status, @Param("today") LocalDate today);

    @Query("SELECT b FROM Borrow b WHERE b.user = :user AND b.status = :status")
    List<Borrow> findByUserAndStatus(@Param("user") User user, @Param("status") BorrowStatus status);

    Optional<Borrow> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, BorrowStatus status);

    long countByUserIdAndStatus(Long userId, BorrowStatus status);

    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, BorrowStatus status);

    @Query("SELECT b FROM Borrow b WHERE " +
            "(b.user.username LIKE %:query% OR b.user.firstName LIKE %:query% OR b.user.lastName LIKE %:query% " +
            "OR b.book.title LIKE %:query% OR b.book.isbn LIKE %:query%)")
    Page<Borrow> searchBorrows(@Param("query") String query, Pageable pageable);
}