package com.preetinest.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryResponseDTO {

    private Long id;
    private String uuid;
    private String name;
    private String description;
    private String metaTitle;
    private String metaKeyword;
    private String metaDescription;
    private String slug;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private List<Long> subCategoryIds;
}