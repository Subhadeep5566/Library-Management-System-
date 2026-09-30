package com.library.management.dto;

import com.library.management.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String isbn;
    private String title;
    private String description;
    private Integer publicationYear;
    private String publisher;
    private String language;
    private Integer pageCount;
    private Integer totalCopies;
    private Integer availableCopies;
    private String shelfLocation;
    private BigDecimal price;
    private BookStatus status;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}