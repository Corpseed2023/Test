package com.preetinest.impl;

import com.preetinest.dto.request.CategoryRequestDTO;
import com.preetinest.dto.response.CategoryResponseDTO;
import com.preetinest.entity.Category;
import com.preetinest.entity.User;
import com.preetinest.repository.CategoryRepository;
import com.preetinest.repository.UserRepository;
import com.preetinest.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO, Long userId) {
        Optional<Category> existingCategory = categoryRepository.findBySlug(requestDTO.getSlug());
        if (existingCategory.isPresent() && existingCategory.get().getDeleteStatus() == 2) {
            throw new IllegalArgumentException("Category with slug " + requestDTO.getSlug() + " already exists");
        }

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can create categories");
            }
        }

        Category category = new Category();
        category.setUuid(UUID.randomUUID().toString());
        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setMetaTitle(requestDTO.getMetaTitle());
        category.setMetaKeyword(requestDTO.getMetaKeyword());
        category.setMetaDescription(requestDTO.getMetaDescription());
        category.setSlug(requestDTO.getSlug());
        category.setActive(requestDTO.isActive());
        category.setDeleteStatus(2);
        category.setCreatedBy(createdBy);

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    @Override
    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(c -> c.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Optional<CategoryResponseDTO> getCategoryByUuid(String uuid) {
        return categoryRepository.findByUuid(uuid)
                .filter(c -> c.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Optional<CategoryResponseDTO> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .filter(c -> c.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public List<CategoryResponseDTO> getAllActiveCategories() {
        return categoryRepository.findAllActiveCategories()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO, Long userId) {
        Category category = categoryRepository.findById(id)
                .filter(c -> c.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        Optional<Category> existingCategory = categoryRepository.findBySlug(requestDTO.getSlug());
        if (existingCategory.isPresent() && existingCategory.get().getDeleteStatus() == 2 &&
                !existingCategory.get().getId().equals(id)) {
            throw new IllegalArgumentException("Slug " + requestDTO.getSlug() + " is already in use by another category");
        }

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can update categories");
            }
        }

        category.setName(requestDTO.getName());
        category.setDescription(requestDTO.getDescription());
        category.setMetaTitle(requestDTO.getMetaTitle());
        category.setMetaKeyword(requestDTO.getMetaKeyword());
        category.setMetaDescription(requestDTO.getMetaDescription());
        category.setSlug(requestDTO.getSlug());
        category.setActive(requestDTO.isActive());
        category.setCreatedBy(createdBy);

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDTO(updatedCategory);
    }

    @Override
    public void softDeleteCategory(Long id, Long userId) {
        Category category = categoryRepository.findById(id)
                .filter(c -> c.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        User user = userRepository.findById(userId)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new IllegalArgumentException("Only ADMIN users can delete categories");
        }

        category.setDeleteStatus(1);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(category.getId());
        responseDTO.setUuid(category.getUuid());
        responseDTO.setName(category.getName());
        responseDTO.setDescription(category.getDescription());
        responseDTO.setMetaTitle(category.getMetaTitle());
        responseDTO.setMetaKeyword(category.getMetaKeyword());
        responseDTO.setMetaDescription(category.getMetaDescription());
        responseDTO.setSlug(category.getSlug());
        responseDTO.setActive(category.isActive());
        responseDTO.setCreatedAt(category.getCreatedAt());
        responseDTO.setUpdatedAt(category.getUpdatedAt());
        responseDTO.setCreatedById(category.getCreatedBy() != null ? category.getCreatedBy().getId() : null);
        return responseDTO;
    }
}