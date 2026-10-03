package com.library.management.service;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.ReturnCreateRequest;
import com.library.management.dto.ReturnResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReturnService {

    ReturnResponse createReturn(ReturnCreateRequest request);

    ReturnResponse getReturnById(Long id);

    PageResponse<ReturnResponse> getAllReturns(Pageable pageable);

    List<ReturnResponse> getReturnsByUser(Long userId);

    List<ReturnResponse> getReturnsByBook(Long bookId);

    PageResponse<ReturnResponse> searchReturns(String query, Pageable pageable);
}