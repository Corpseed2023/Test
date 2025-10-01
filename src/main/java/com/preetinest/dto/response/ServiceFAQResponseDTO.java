package com.preetinest.dto.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceFAQResponseDTO {

    private Long id;
    private String uuid;
    private String question;
    private String answer;
    private Integer displayOrder;
    private Long serviceId;
    private boolean active;
    private boolean displayStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
}