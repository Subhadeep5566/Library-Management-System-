package com.library.management.desktop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class BorrowCreateRequest {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("bookId")
    private Long bookId;

    @JsonProperty("borrowDate")
    private LocalDate borrowDate;

    @JsonProperty("dueDate")
    private LocalDate dueDate;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}