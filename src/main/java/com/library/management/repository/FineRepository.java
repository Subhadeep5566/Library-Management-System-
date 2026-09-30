package com.library.management.repository;

import com.library.management.entity.Fine;
import com.library.management.entity.FineStatus;
import com.library.management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByUserId(Long userId);

    Page<Fine> findByUserId(Long userId, Pageable pageable);

    List<Fine> findByStatus(FineStatus status);

    List<Fine> findByUserIdAndStatus(Long userId, FineStatus status);

    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.user = :user AND f.status = :status")
    BigDecimal getTotalUnpaidFinesByUser(@Param("user") User user, @Param("status") FineStatus status);

    @Query("SELECT f FROM Fine f WHERE f.borrow = :borrow")
    Optional<Fine> findByBorrowId(@Param("borrow") Long borrowId);

    boolean existsByBorrowId(Long borrowId);
}