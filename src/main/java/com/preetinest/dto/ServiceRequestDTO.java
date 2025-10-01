package com.preetinest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Subcategory ID is required")
    private Long subCategoryId;

    @Size(max = 255, message = "Icon URL must not exceed 255 characters")
    private String iconUrl;

    @Size(max = 255, message = "Image must not exceed 255 characters")
    private String image;

    @NotBlank(message = "Meta title is required")
    @Size(max = 255, message = "Meta title must not exceed 255 characters")
    private String metaTitle;

    @NotBlank(message = "Meta keyword is required")
    private String metaKeyword;

    @NotBlank(message = "Meta description is required")
    private String metaDescription;

    @NotBlank(message = "Slug is required")
    @Size(max = 100, message = "Slug must not exceed 100 characters")
    private String slug;

    private boolean active = true;

    private boolean inactive = false;

    private boolean displayStatus = true;

    private boolean showOnHome = false;
}