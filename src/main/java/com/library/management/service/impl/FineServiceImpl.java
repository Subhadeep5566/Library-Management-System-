package com.library.management.service.impl;

import com.library.management.dto.FineResponse;
import com.library.management.dto.PageResponse;
import com.library.management.entity.Fine;
import com.library.management.entity.FineStatus;
import com.library.management.entity.User;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.exception.BusinessLogicException;
import com.library.management.repository.FineRepository;
import com.library.management.repository.UserRepository;
import com.library.management.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public FineResponse getFineById(Long id) {
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found with id: " + id));
        return mapToResponse(fine);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FineResponse> getAllFines(Pageable pageable) {
        Page<Fine> fines = fineRepository.findAll(pageable);
        return mapToPageResponse(fines);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FineResponse> getFinesByUser(Long userId, Pageable pageable) {
        Page<Fine> fines = fineRepository.findByUserId(userId, pageable);
        return mapToPageResponse(fines);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getFinesByUserAndStatus(Long userId, FineStatus status) {
        return fineRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalUnpaidFinesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        BigDecimal total = fineRepository.getTotalUnpaidFinesByUser(user, FineStatus.PENDING);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public FineResponse payFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found with id: " + fineId));

        if (fine.getStatus() == FineStatus.PAID) {
            throw new BusinessLogicException("Fine already paid");
        }

        if (fine.getStatus() == FineStatus.WAIVED) {
            throw new BusinessLogicException("Fine has been waived");
        }

        fine.setStatus(FineStatus.PAID);
        fine.setPaidDate(LocalDate.now());

        Fine savedFine = fineRepository.save(fine);
        return mapToResponse(savedFine);
    }

    @Override
    public FineResponse waiveFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found with id: " + fineId));

        if (fine.getStatus() == FineStatus.PAID) {
            throw new BusinessLogicException("Cannot waive a paid fine");
        }

        fine.setStatus(FineStatus.WAIVED);
        fine.setPaidDate(LocalDate.now());

        Fine savedFine = fineRepository.save(fine);
        return mapToResponse(savedFine);
    }

    @Override
    @Transactional
    public void generateFinesForOverdueBooks() {
        // This would be called by a scheduled task
        // Implementation would find overdue borrows and create fines
    }

    private FineResponse mapToResponse(Fine fine) {
        return FineResponse.builder()
                .id(fine.getId())
                .userId(fine.getUser().getId())
                .userName(fine.getUser().getFullName())
                .borrowId(fine.getBorrow() != null ? fine.getBorrow().getId() : null)
                .bookTitle(fine.getBorrow() != null ? fine.getBorrow().getBook().getTitle() : null)
                .amount(fine.getAmount())
                .fineDate(fine.getFineDate())
                .paidDate(fine.getPaidDate())
                .status(fine.getStatus())
                .daysOverdue(fine.getDaysOverdue())
                .reason(fine.getReason())
                .createdAt(fine.getCreatedAt())
                .updatedAt(fine.getUpdatedAt())
                .build();
    }

    private PageResponse<FineResponse> mapToPageResponse(Page<Fine> fines) {
        return PageResponse.<FineResponse>builder()
                .content(fines.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(fines.getNumber())
                .pageSize(fines.getSize())
                .totalElements(fines.getTotalElements())
                .totalPages(fines.getTotalPages())
                .first(fines.isFirst())
                .last(fines.isLast())
                .empty(fines.isEmpty())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FineResponse> searchFines(String query, Pageable pageable) {
        Page<Fine> fines = fineRepository.searchFines(query, pageable);
        return mapToPageResponse(fines);
    }
}