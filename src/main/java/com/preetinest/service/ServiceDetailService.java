package com.preetinest.service;

import com.preetinest.dto.request.ServiceDetailRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceDetailService {
    Optional<Map<String, Object>> getServiceDetailById(Long id);
    Optional<Map<String, Object>> getServiceDetailByUuid(String uuid);
    List<Map<String, Object>> getServiceDetailsByServiceId(Long serviceId);
    void softDeleteServiceDetail(Long id, Long userId);
    Map<String, Object> createServiceDetail(ServiceDetailRequestDTO requestDTO, Long userId);
    Map<String, Object> updateServiceDetail(Long id, ServiceDetailRequestDTO requestDTO, Long userId);
}