package com.library.management.service;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.PageResponse;
import com.library.management.dto.CategoryCreateRequest;
import com.library.management.dto.CategoryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request);

    CategoryResponse getCategoryById(Long id);

    PageResponse<CategoryResponse> getAllCategories(Pageable pageable);

    PageResponse<CategoryResponse> searchCategories(String search, Pageable pageable);

    CategoryResponse updateCategory(Long id, CategoryCreateRequest request);

    void deleteCategory(Long id);

    boolean existsByName(String name);
}