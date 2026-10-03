package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.ReturnCreateRequest;
import com.library.management.dto.ReturnResponse;
import com.library.management.service.ReturnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
public class ReturnController {

    private final ReturnService returnService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReturnResponse>> createReturn(@Valid @RequestBody ReturnCreateRequest request) {
        ReturnResponse bookReturn = returnService.createReturn(request);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", bookReturn));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnResponse>> getReturnById(@PathVariable Long id) {
        ReturnResponse bookReturn = returnService.getReturnById(id);
        return ResponseEntity.ok(ApiResponse.success(bookReturn));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReturnResponse>>> getAllReturns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<ReturnResponse> returns = returnService.getAllReturns(pageable);
        return ResponseEntity.ok(ApiResponse.success(returns));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReturnResponse>>> getReturnsByUser(@PathVariable Long userId) {
        List<ReturnResponse> returns = returnService.getReturnsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(returns));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ReturnResponse>>> searchReturns(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ReturnResponse> returns = returnService.searchReturns(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(returns));
    }
}