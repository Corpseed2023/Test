package com.preetinest.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {

    private Long id;
    private String uuid;
    private String name;
    private String description;
    private Long subCategoryId;
    private String subCategoryName;
    private Long categoryId;
    private String categoryName;
    private String iconUrl;
    private String image;
    private String metaTitle;
    private String metaKeyword;
    private String metaDescription;
    private String slug;
    private boolean active;
    private boolean displayStatus;
    private boolean showOnHome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private List<ServiceDetailResponseDTO> serviceDetails;
}