package com.library.management.service.impl;

import com.library.management.dto.BorrowResponse;
import com.library.management.dto.BorrowCreateRequest;
import com.library.management.dto.PageResponse;
import com.library.management.entity.Borrow;
import com.library.management.entity.BorrowStatus;
import com.library.management.entity.Book;
import com.library.management.entity.BookStatus;
import com.library.management.entity.User;
import com.library.management.entity.Fine;
import com.library.management.entity.FineStatus;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.exception.BusinessLogicException;
import com.library.management.repository.BorrowRepository;
import com.library.management.repository.BookRepository;
import com.library.management.repository.UserRepository;
import com.library.management.repository.FineRepository;
import com.library.management.service.BorrowService;
import com.library.management.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final FineRepository fineRepository;
    private final FineService fineService;

    private static final BigDecimal FINE_PER_DAY = new BigDecimal("1.00");
    private static final int DEFAULT_BORROW_DAYS = 14;

    @Override
    public BorrowResponse createBorrow(BorrowCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (user.getStatus() != com.library.management.entity.UserStatus.ACTIVE) {
            throw new BusinessLogicException("User is not active");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessLogicException("No available copies of this book");
        }

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BusinessLogicException("Book is not available for borrowing");
        }

        if (borrowRepository.existsByUserIdAndBookIdAndStatus(request.getUserId(), request.getBookId(), BorrowStatus.BORROWED)) {
            throw new BusinessLogicException("User already has an active borrow for this book");
        }

        long activeBorrows = borrowRepository.countByUserIdAndStatus(request.getUserId(), BorrowStatus.BORROWED);
        if (activeBorrows >= 5) {
            throw new BusinessLogicException("User has reached maximum active borrows limit (5)");
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : borrowDate.plusDays(DEFAULT_BORROW_DAYS);

        Borrow borrow = Borrow.builder()
                .user(user)
                .book(book)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(BorrowStatus.BORROWED)
                .notes(request.getNotes())
                .build();

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setStatus(BookStatus.BORROWED);
        }
        bookRepository.save(book);

        Borrow savedBorrow = borrowRepository.save(borrow);
        return mapToResponse(savedBorrow);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowResponse getBorrowById(Long id) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow not found with id: " + id));
        return mapToResponse(borrow);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowResponse> getAllBorrows(Pageable pageable) {
        Page<Borrow> borrows = borrowRepository.findAll(pageable);
        return mapToPageResponse(borrows);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowResponse> getBorrowsByUser(Long userId, Pageable pageable) {
        Page<Borrow> borrows = borrowRepository.findByUserId(userId, pageable);
        return mapToPageResponse(borrows);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowResponse> getBorrowsByUserAndStatus(Long userId, BorrowStatus status) {
        return borrowRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowResponse> getBorrowsByBook(Long bookId) {
        return borrowRepository.findByBookId(bookId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowResponse> getOverdueBorrows() {
        return borrowRepository.findOverdueBorrows(BorrowStatus.BORROWED, LocalDate.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BorrowResponse returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow not found with id: " + borrowId));

        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessLogicException("Book already returned");
        }

        borrow.setStatus(BorrowStatus.RETURNED);
        borrow.setReturnDate(LocalDate.now());

        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if (book.getAvailableCopies() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        bookRepository.save(book);

        // Generate fine if overdue
        if (borrow.isOverdue()) {
            long overdueDays = borrow.getOverdueDays();
            BigDecimal fineAmount = FINE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays));
            
            Fine fine = Fine.builder()
                    .user(borrow.getUser())
                    .borrow(borrow)
                    .amount(fineAmount)
                    .daysOverdue((int) overdueDays)
                    .reason("Overdue return by " + overdueDays + " days")
                    .status(FineStatus.PENDING)
                    .build();
            fineRepository.save(fine);
        }

        Borrow savedBorrow = borrowRepository.save(borrow);
        return mapToResponse(savedBorrow);
    }

    @Override
    public BorrowResponse markAsLost(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow not found with id: " + borrowId));

        if (borrow.getStatus() == BorrowStatus.RETURNED) {
            throw new BusinessLogicException("Cannot mark returned book as lost");
        }

        borrow.setStatus(BorrowStatus.LOST);

        Book book = borrow.getBook();
        book.setTotalCopies(book.getTotalCopies() - 1);
        if (book.getTotalCopies() == 0) {
            book.setStatus(BookStatus.LOST);
        }
        bookRepository.save(book);

        // Generate fine for lost book (full price)
        Fine fine = Fine.builder()
                .user(borrow.getUser())
                .borrow(borrow)
                .amount(book.getPrice() != null ? book.getPrice() : BigDecimal.ZERO)
                .daysOverdue(0)
                .reason("Book marked as lost")
                .status(FineStatus.PENDING)
                .build();
        fineRepository.save(fine);

        Borrow savedBorrow = borrowRepository.save(borrow);
        return mapToResponse(savedBorrow);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveBorrowsByUser(Long userId) {
        return borrowRepository.countByUserIdAndStatus(userId, BorrowStatus.BORROWED);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveBorrow(Long userId, Long bookId) {
        return borrowRepository.existsByUserIdAndBookIdAndStatus(userId, bookId, BorrowStatus.BORROWED);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowResponse> searchBorrows(String query, Pageable pageable) {
        Page<Borrow> borrows = borrowRepository.searchBorrows(query, pageable);
        return mapToPageResponse(borrows);
    }

    private BorrowResponse mapToResponse(Borrow borrow) {
        return BorrowResponse.builder()
                .id(borrow.getId())
                .userId(borrow.getUser().getId())
                .userName(borrow.getUser().getFullName())
                .bookId(borrow.getBook().getId())
                .bookTitle(borrow.getBook().getTitle())
                .bookIsbn(borrow.getBook().getIsbn())
                .borrowDate(borrow.getBorrowDate())
                .dueDate(borrow.getDueDate())
                .returnDate(borrow.getReturnDate())
                .status(borrow.getStatus())
                .notes(borrow.getNotes())
                .overdue(borrow.isOverdue())
                .overdueDays(borrow.getOverdueDays())
                .createdAt(borrow.getCreatedAt())
                .updatedAt(borrow.getUpdatedAt())
                .build();
    }

    private PageResponse<BorrowResponse> mapToPageResponse(Page<Borrow> borrows) {
        return PageResponse.<BorrowResponse>builder()
                .content(borrows.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(borrows.getNumber())
                .pageSize(borrows.getSize())
                .totalElements(borrows.getTotalElements())
                .totalPages(borrows.getTotalPages())
                .first(borrows.isFirst())
                .last(borrows.isLast())
                .empty(borrows.isEmpty())
                .build();
    }
}