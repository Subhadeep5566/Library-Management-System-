package com.library.management.dto;

import com.library.management.entity.FineStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long borrowId;
    private String bookTitle;
    private BigDecimal amount;
    private LocalDate fineDate;
    private LocalDate paidDate;
    private FineStatus status;
    private Integer daysOverdue;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}