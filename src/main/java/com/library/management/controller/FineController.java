package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.FineResponse;
import com.library.management.entity.FineStatus;
import com.library.management.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FineResponse>> getFineById(@PathVariable Long id) {
        FineResponse fine = fineService.getFineById(id);
        return ResponseEntity.ok(ApiResponse.success(fine));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<FineResponse>>> getAllFines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<FineResponse> fines = fineService.getAllFines(pageable);
        return ResponseEntity.ok(ApiResponse.success(fines));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<FineResponse>>> getFinesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<FineResponse> fines = fineService.getFinesByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(fines));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<List<FineResponse>>> getFinesByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable FineStatus status) {
        List<FineResponse> fines = fineService.getFinesByUserAndStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(fines));
    }

    @GetMapping("/user/{userId}/total-unpaid")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalUnpaidFinesByUser(@PathVariable Long userId) {
        BigDecimal total = fineService.getTotalUnpaidFinesByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<FineResponse>> payFine(@PathVariable Long id) {
        FineResponse fine = fineService.payFine(id);
        return ResponseEntity.ok(ApiResponse.success("Fine paid successfully", fine));
    }

    @PostMapping("/{id}/waive")
    public ResponseEntity<ApiResponse<FineResponse>> waiveFine(@PathVariable Long id) {
        FineResponse fine = fineService.waiveFine(id);
        return ResponseEntity.ok(ApiResponse.success("Fine waived successfully", fine));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Void>> generateFinesForOverdueBooks() {
        fineService.generateFinesForOverdueBooks();
        return ResponseEntity.ok(ApiResponse.success("Fines generated for overdue books"));
    }
}