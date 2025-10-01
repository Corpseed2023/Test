package com.preetinest.service;


import com.preetinest.dto.ServiceRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceService {
    Optional<Map<String, Object>> getServiceById(Long id);
    Optional<Map<String, Object>> getServiceByUuid(String uuid);
    Optional<Map<String, Object>> getServiceBySlug(String slug);
    List<Map<String, Object>> getAllActiveServices();
    Optional<Map<String, Object>> getServiceWithDetailsById(Long id);
    void softDeleteService(Long id, Long userId);
    Map<String, Object> createService(ServiceRequestDTO requestDTO, Long userId);
    Map<String, Object> updateService(Long id, ServiceRequestDTO requestDTO, Long userId);
}