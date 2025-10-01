package com.preetinest.impl;

import com.preetinest.dto.SubCategoryRequestDTO;
import com.preetinest.entity.Category;
import com.preetinest.entity.SubCategory;
import com.preetinest.entity.User;
import com.preetinest.repository.CategoryRepository;
import com.preetinest.repository.SubCategoryRepository;
import com.preetinest.repository.UserRepository;
import com.preetinest.service.SubCategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository,
                                  CategoryRepository categoryRepository,
                                  UserRepository userRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> createSubCategory(SubCategoryRequestDTO requestDTO, Long userId) {
        Optional<SubCategory> existingSubCategory = subCategoryRepository.findBySlug(requestDTO.getSlug());
        if (existingSubCategory.isPresent() && existingSubCategory.get().getDeleteStatus() == 2) {
            throw new IllegalArgumentException("Subcategory with slug " + requestDTO.getSlug() + " already exists");
        }

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can create subcategories");
            }
        }

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .filter(c -> c.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        SubCategory subCategory = new SubCategory();
        subCategory.setUuid(UUID.randomUUID().toString());
        subCategory.setName(requestDTO.getName());
        subCategory.setDescription(requestDTO.getDescription());
        subCategory.setMetaTitle(requestDTO.getMetaTitle());
        subCategory.setMetaKeyword(requestDTO.getMetaKeyword());
        subCategory.setMetaDescription(requestDTO.getMetaDescription());
        subCategory.setSlug(requestDTO.getSlug());
        subCategory.setActive(requestDTO.isActive());
        subCategory.setDisplayStatus(requestDTO.isDisplayStatus());
        subCategory.setDeleteStatus(2);
        subCategory.setCreatedBy(createdBy);
        subCategory.setCategory(category);

        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        return mapToResponseDTO(savedSubCategory);
    }

    @Override
    public Optional<Map<String, Object>> getSubCategoryById(Long id) {
        return subCategoryRepository.findById(id)
                .filter(sc -> sc.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Optional<Map<String, Object>> getSubCategoryByUuid(String uuid) {
        return subCategoryRepository.findByUuid(uuid)
                .filter(sc -> sc.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Optional<Map<String, Object>> getSubCategoryBySlug(String slug) {
        return subCategoryRepository.findBySlug(slug)
                .filter(sc -> sc.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public List<Map<String, Object>> getAllActiveSubCategories() {
        return subCategoryRepository.findAllActiveSubCategories()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> updateSubCategory(Long id, SubCategoryRequestDTO requestDTO, Long userId) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .filter(sc -> sc.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + id));

        Optional<SubCategory> existingSubCategory = subCategoryRepository.findBySlug(requestDTO.getSlug());
        if (existingSubCategory.isPresent() && existingSubCategory.get().getDeleteStatus() == 2 &&
                !existingSubCategory.get().getId().equals(id)) {
            throw new IllegalArgumentException("Slug " + requestDTO.getSlug() + " is already in use by another subcategory");
        }

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can update subcategories");
            }
        }

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .filter(c -> c.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDTO.getCategoryId()));

        subCategory.setName(requestDTO.getName());
        subCategory.setDescription(requestDTO.getDescription());
        subCategory.setMetaTitle(requestDTO.getMetaTitle());
        subCategory.setMetaKeyword(requestDTO.getMetaKeyword());
        subCategory.setMetaDescription(requestDTO.getMetaDescription());
        subCategory.setSlug(requestDTO.getSlug());
        subCategory.setActive(requestDTO.isActive());
        subCategory.setDisplayStatus(requestDTO.isDisplayStatus());
        subCategory.setCreatedBy(createdBy);
        subCategory.setCategory(category);

        SubCategory updatedSubCategory = subCategoryRepository.save(subCategory);
        return mapToResponseDTO(updatedSubCategory);
    }

    @Override
    public void softDeleteSubCategory(Long id, Long userId) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .filter(sc -> sc.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + id));

        User user = userRepository.findById(userId)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new IllegalArgumentException("Only ADMIN users can delete subcategories");
        }

        subCategory.setDeleteStatus(1);
        subCategory.setActive(false);
        subCategory.setDisplayStatus(false);
        subCategoryRepository.save(subCategory);
    }

    private Map<String, Object> mapToResponseDTO(SubCategory subCategory) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", subCategory.getId());
        response.put("uuid", subCategory.getUuid());
        response.put("name", subCategory.getName());
        response.put("description", subCategory.getDescription());
        response.put("metaTitle", subCategory.getMetaTitle());
        response.put("metaKeyword", subCategory.getMetaKeyword());
        response.put("metaDescription", subCategory.getMetaDescription());
        response.put("slug", subCategory.getSlug());
        response.put("active", subCategory.isActive());
        response.put("displayStatus", subCategory.isDisplayStatus());
        response.put("createdAt", subCategory.getCreatedAt());
        response.put("updatedAt", subCategory.getUpdatedAt());
        response.put("createdById", subCategory.getCreatedBy() != null ? subCategory.getCreatedBy().getId() : null);
        response.put("categoryId", subCategory.getCategory().getId());
        return response;
    }
}