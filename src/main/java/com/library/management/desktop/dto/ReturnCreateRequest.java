package com.library.management.desktop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class ReturnCreateRequest {

    @JsonProperty("borrowId")
    private Long borrowId;

    @JsonProperty("processedById")
    private Long processedById;

    @JsonProperty("returnDate")
    private java.time.LocalDate returnDate;

    @JsonProperty("condition")
    private String condition;

    @JsonProperty("notes")
    private String notes;

    public Long getBorrowId() { return borrowId; }
    public void setBorrowId(Long borrowId) { this.borrowId = borrowId; }
    public Long getProcessedById() { return processedById; }
    public void setProcessedById(Long processedById) { this.processedById = processedById; }
    public java.time.LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(java.time.LocalDate returnDate) { this.returnDate = returnDate; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}