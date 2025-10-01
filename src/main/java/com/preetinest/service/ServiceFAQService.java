package com.preetinest.service;

import com.preetinest.dto.request.ServiceFAQRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceFAQService {
    Optional<Map<String, Object>> getServiceFAQById(Long id);
    Optional<Map<String, Object>> getServiceFAQByUuid(String uuid);
    List<Map<String, Object>> getServiceFAQsByServiceId(Long serviceId);
    void softDeleteServiceFAQ(Long id, Long userId);
    Map<String, Object> createServiceFAQ(ServiceFAQRequestDTO requestDTO, Long userId);
    Map<String, Object> updateServiceFAQ(Long id, ServiceFAQRequestDTO requestDTO, Long userId);
}