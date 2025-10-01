package com.preetinest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceDetailRequestDTO {

    @NotBlank(message = "Heading is required")
    @Size(max = 255, message = "Heading must not exceed 255 characters")
    private String heading;

    private String details;

    @NotNull(message = "Display order is required")
    @Min(value = 0, message = "Display order must be a non-negative integer")
    private Integer displayOrder;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private boolean active = true;
}