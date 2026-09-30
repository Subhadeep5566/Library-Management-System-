package com.library.management.service.impl;

import com.library.management.dto.ReturnResponse;
import com.library.management.dto.ReturnCreateRequest;
import com.library.management.dto.PageResponse;
import com.library.management.entity.Return;
import com.library.management.entity.ReturnCondition;
import com.library.management.entity.Borrow;
import com.library.management.entity.BorrowStatus;
import com.library.management.entity.User;
import com.library.management.entity.Book;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.exception.BusinessLogicException;
import com.library.management.repository.ReturnRepository;
import com.library.management.repository.BorrowRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;

    @Override
    public ReturnResponse createReturn(ReturnCreateRequest request) {
        Borrow borrow = borrowRepository.findById(request.getBorrowId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrow not found with id: " + request.getBorrowId()));

        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessLogicException("Book already returned");
        }

        if (borrow.getStatus() == BorrowStatus.LOST) {
            throw new BusinessLogicException("Cannot return a lost book");
        }

        User processedBy = userRepository.findById(request.getProcessedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getProcessedById()));

        Return bookReturn = Return.builder()
                .borrow(borrow)
                .processedBy(processedBy)
                .returnDate(request.getReturnDate() != null ? request.getReturnDate() : LocalDate.now())
                .condition(request.getCondition() != null ? request.getCondition() : ReturnCondition.GOOD)
                .notes(request.getNotes())
                .build();

        // Update borrow status
        borrow.setStatus(BorrowStatus.RETURNED);
        borrow.setReturnDate(bookReturn.getReturnDate());
        borrowRepository.save(borrow);

        // Update book availability
        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getAvailableCopies() > 0) {
            book.setStatus(com.library.management.entity.BookStatus.AVAILABLE);
        }

        Return savedReturn = returnRepository.save(bookReturn);
        return mapToResponse(savedReturn);
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnResponse getReturnById(Long id) {
        Return bookReturn = returnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Return not found with id: " + id));
        return mapToResponse(bookReturn);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReturnResponse> getAllReturns(Pageable pageable) {
        Page<Return> returns = returnRepository.findAll(pageable);
        return mapToPageResponse(returns);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnResponse> getReturnsByUser(Long userId) {
        return returnRepository.findByProcessedById(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnResponse> getReturnsByBook(Long bookId) {
        // This would need a custom query in the repository
        return List.of();
    }

    private ReturnResponse mapToResponse(Return bookReturn) {
        return ReturnResponse.builder()
                .id(bookReturn.getId())
                .borrowId(bookReturn.getBorrow().getId())
                .bookTitle(bookReturn.getBorrow().getBook().getTitle())
                .userName(bookReturn.getBorrow().getUser().getFullName())
                .returnDate(bookReturn.getReturnDate())
                .condition(bookReturn.getCondition())
                .notes(bookReturn.getNotes())
                .processedById(bookReturn.getProcessedBy().getId())
                .processedByName(bookReturn.getProcessedBy().getFullName())
                .createdAt(bookReturn.getCreatedAt())
                .build();
    }

    private PageResponse<ReturnResponse> mapToPageResponse(Page<Return> returns) {
        return PageResponse.<ReturnResponse>builder()
                .content(returns.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(returns.getNumber())
                .pageSize(returns.getSize())
                .totalElements(returns.getTotalElements())
                .totalPages(returns.getTotalPages())
                .first(returns.isFirst())
                .last(returns.isLast())
                .empty(returns.isEmpty())
                .build();
    }
}