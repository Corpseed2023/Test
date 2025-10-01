package com.preetinest.service;



import com.preetinest.dto.request.CategoryRequestDTO;
import com.preetinest.dto.response.CategoryResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<CategoryResponseDTO> getCategoryById(Long id);
    Optional<CategoryResponseDTO> getCategoryByUuid(String uuid);
    Optional<CategoryResponseDTO> getCategoryBySlug(String slug);
    List<CategoryResponseDTO> getAllActiveCategories();
    void softDeleteCategory(Long id, Long userId);

    CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO, Long userId);

    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO, Long userId);
}