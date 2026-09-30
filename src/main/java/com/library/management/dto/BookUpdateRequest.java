package com.library.management.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @Min(value = 1000, message = "Publication year must be after 1000")
    @Max(value = 2100, message = "Publication year must be before 2100")
    private Integer publicationYear;

    @Size(max = 100, message = "Publisher must not exceed 100 characters")
    private String publisher;

    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;

    @Min(value = 1, message = "Page count must be at least 1")
    private Integer pageCount;

    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    @Size(max = 50, message = "Shelf location must not exceed 50 characters")
    private String shelfLocation;

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    private Long authorId;

    private Long categoryId;
}