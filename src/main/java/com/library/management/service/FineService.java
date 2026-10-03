package com.library.management.service;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.FineResponse;
import com.library.management.entity.FineStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface FineService {

    FineResponse getFineById(Long id);

    PageResponse<FineResponse> getAllFines(Pageable pageable);

    PageResponse<FineResponse> getFinesByUser(Long userId, Pageable pageable);

    List<FineResponse> getFinesByUserAndStatus(Long userId, FineStatus status);

    BigDecimal getTotalUnpaidFinesByUser(Long userId);

    FineResponse payFine(Long fineId);

    FineResponse waiveFine(Long fineId);

    void generateFinesForOverdueBooks();

    PageResponse<FineResponse> searchFines(String query, Pageable pageable);
}