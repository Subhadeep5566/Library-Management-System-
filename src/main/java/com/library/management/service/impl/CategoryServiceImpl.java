package com.library.management.service.impl;

import com.library.management.dto.CategoryResponse;
import com.library.management.dto.CategoryCreateRequest;
import com.library.management.dto.PageResponse;
import com.library.management.entity.Category;
import com.library.management.exception.ResourceNotFoundException;
import com.library.management.exception.DuplicateResourceException;
import com.library.management.repository.CategoryRepository;
import com.library.management.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return mapToPageResponse(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> searchCategories(String search, Pageable pageable) {
        Page<Category> categories = categoryRepository.searchCategories(search, pageable);
        return mapToPageResponse(categories);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryCreateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new DuplicateResourceException("Category already exists with name: " + request.getName());
            }
            category.setName(request.getName());
        }
        if (request.getDescription() != null) category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .bookCount(category.getBooks() != null ? category.getBooks().size() : 0)
                .build();
    }

    private PageResponse<CategoryResponse> mapToPageResponse(Page<Category> categories) {
        return PageResponse.<CategoryResponse>builder()
                .content(categories.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(categories.getNumber())
                .pageSize(categories.getSize())
                .totalElements(categories.getTotalElements())
                .totalPages(categories.getTotalPages())
                .first(categories.isFirst())
                .last(categories.isLast())
                .empty(categories.isEmpty())
                .build();
    }
}