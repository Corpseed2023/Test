package com.preetinest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

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
}