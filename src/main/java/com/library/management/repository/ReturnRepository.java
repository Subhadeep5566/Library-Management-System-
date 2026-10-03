package com.library.management.repository;

import com.library.management.entity.Return;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Long> {

    Optional<Return> findByBorrowId(Long borrowId);

    List<Return> findByProcessedById(Long userId);

    Page<Return> findByProcessedById(Long userId, Pageable pageable);

    @Query("SELECT r FROM Return r WHERE " +
            "(r.borrow.user.username LIKE %:query% OR r.borrow.user.firstName LIKE %:query% OR r.borrow.user.lastName LIKE %:query% " +
            "OR r.borrow.book.title LIKE %:query% OR r.notes LIKE %:query%)")
    Page<Return> searchReturns(@Param("query") String query, Pageable pageable);
}