package com.preetinest.service;



import com.preetinest.dto.SubCategoryRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SubCategoryService {
    Optional<Map<String, Object>> getSubCategoryById(Long id);
    Optional<Map<String, Object>> getSubCategoryByUuid(String uuid);
    Optional<Map<String, Object>> getSubCategoryBySlug(String slug);
    List<Map<String, Object>> getAllActiveSubCategories();
    void softDeleteSubCategory(Long id, Long userId);
    Map<String, Object> createSubCategory(SubCategoryRequestDTO requestDTO, Long userId);
    Map<String, Object> updateSubCategory(Long id, SubCategoryRequestDTO requestDTO, Long userId);
}