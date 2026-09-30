package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.BorrowCreateRequest;
import com.library.management.dto.BorrowResponse;
import com.library.management.entity.BorrowStatus;
import com.library.management.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    public ResponseEntity<ApiResponse<BorrowResponse>> createBorrow(@Valid @RequestBody BorrowCreateRequest request) {
        BorrowResponse borrow = borrowService.createBorrow(request);
        return ResponseEntity.ok(ApiResponse.success("Book borrowed successfully", borrow));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowResponse>> getBorrowById(@PathVariable Long id) {
        BorrowResponse borrow = borrowService.getBorrowById(id);
        return ResponseEntity.ok(ApiResponse.success(borrow));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BorrowResponse>>> getAllBorrows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<BorrowResponse> borrows = borrowService.getAllBorrows(pageable);
        return ResponseEntity.ok(ApiResponse.success(borrows));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<BorrowResponse>>> getBorrowsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BorrowResponse> borrows = borrowService.getBorrowsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(borrows));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getBorrowsByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable BorrowStatus status) {
        List<BorrowResponse> borrows = borrowService.getBorrowsByUserAndStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.success(borrows));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getBorrowsByBook(@PathVariable Long bookId) {
        List<BorrowResponse> borrows = borrowService.getBorrowsByBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(borrows));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getOverdueBorrows() {
        List<BorrowResponse> borrows = borrowService.getOverdueBorrows();
        return ResponseEntity.ok(ApiResponse.success(borrows));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<BorrowResponse>> returnBook(@PathVariable Long id) {
        BorrowResponse borrow = borrowService.returnBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", borrow));
    }

    @PostMapping("/{id}/lost")
    public ResponseEntity<ApiResponse<BorrowResponse>> markAsLost(@PathVariable Long id) {
        BorrowResponse borrow = borrowService.markAsLost(id);
        return ResponseEntity.ok(ApiResponse.success("Book marked as lost", borrow));
    }

    @GetMapping("/user/{userId}/active-count")
    public ResponseEntity<ApiResponse<Long>> getActiveBorrowsCount(@PathVariable Long userId) {
        long count = borrowService.countActiveBorrowsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/user/{userId}/book/{bookId}/active")
    public ResponseEntity<ApiResponse<Boolean>> hasActiveBorrow(
            @PathVariable Long userId,
            @PathVariable Long bookId) {
        boolean hasActive = borrowService.hasActiveBorrow(userId, bookId);
        return ResponseEntity.ok(ApiResponse.success(hasActive));
    }
}