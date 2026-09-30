package com.library.management.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorCreateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 1000, message = "Biography must not exceed 1000 characters")
    private String biography;

    private LocalDate birthDate;

    private LocalDate deathDate;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;
}