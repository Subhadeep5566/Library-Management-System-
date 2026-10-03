package com.library.management.desktop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BookCreateRequest {

    @JsonProperty("title")
    private String title;

    @JsonProperty("isbn")
    private String isbn;

    @JsonProperty("authorId")
    private Long authorId;

    @JsonProperty("categoryId")
    private Long categoryId;

    @JsonProperty("totalCopies")
    private Integer totalCopies;

    @JsonProperty("availableCopies")
    private Integer availableCopies;

    @JsonProperty("publicationYear")
    private Integer publicationYear;

    @JsonProperty("publisher")
    private String publisher;

    @JsonProperty("language")
    private String language;

    @JsonProperty("pageCount")
    private Integer pageCount;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("shelfLocation")
    private String shelfLocation;

    @JsonProperty("description")
    private String description;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getShelfLocation() { return shelfLocation; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}