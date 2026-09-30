package com.library.management.dto;

import com.library.management.entity.ReturnCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {

    private Long id;
    private Long borrowId;
    private String bookTitle;
    private String userName;
    private LocalDate returnDate;
    private ReturnCondition condition;
    private String notes;
    private Long processedById;
    private String processedByName;
    private LocalDateTime createdAt;
}