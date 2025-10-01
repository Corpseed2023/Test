package com.preetinest.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetailResponseDTO {

    private Long id;
    private String uuid;
    private String heading;
    private String details;
    private Integer displayOrder;
    private Long serviceId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
}