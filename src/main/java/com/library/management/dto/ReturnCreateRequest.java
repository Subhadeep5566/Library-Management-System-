package com.library.management.dto;

import com.library.management.entity.ReturnCondition;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnCreateRequest {

    @NotNull(message = "Borrow ID is required")
    private Long borrowId;

    @NotNull(message = "Processed by user ID is required")
    private Long processedById;

    private LocalDate returnDate;

    private ReturnCondition condition = ReturnCondition.GOOD;

    private String notes;
}