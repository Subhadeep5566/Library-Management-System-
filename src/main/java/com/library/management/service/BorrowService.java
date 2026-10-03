package com.library.management.service;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.BorrowCreateRequest;
import com.library.management.dto.BorrowResponse;
import com.library.management.entity.BorrowStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BorrowService {

    BorrowResponse createBorrow(BorrowCreateRequest request);

    BorrowResponse getBorrowById(Long id);

    PageResponse<BorrowResponse> getAllBorrows(Pageable pageable);

    PageResponse<BorrowResponse> getBorrowsByUser(Long userId, Pageable pageable);

    List<BorrowResponse> getBorrowsByUserAndStatus(Long userId, BorrowStatus status);

    List<BorrowResponse> getBorrowsByBook(Long bookId);

    List<BorrowResponse> getOverdueBorrows();

    BorrowResponse returnBook(Long borrowId);

    BorrowResponse markAsLost(Long borrowId);

    long countActiveBorrowsByUser(Long userId);

    boolean hasActiveBorrow(Long userId, Long bookId);

    PageResponse<BorrowResponse> searchBorrows(String query, Pageable pageable);
}