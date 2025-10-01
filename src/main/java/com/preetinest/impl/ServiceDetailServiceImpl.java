package com.preetinest.impl;

import com.preetinest.dto.request.ServiceDetailRequestDTO;
import com.preetinest.dto.response.ServiceDetailResponseDTO;
import com.preetinest.entity.ServiceDetail;
import com.preetinest.entity.Services;
import com.preetinest.entity.User;
import com.preetinest.repository.ServiceDetailRepository;
import com.preetinest.repository.ServiceRepository;
import com.preetinest.repository.UserRepository;
import com.preetinest.service.ServiceDetailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceDetailServiceImpl implements ServiceDetailService {

    private final ServiceDetailRepository serviceDetailRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public ServiceDetailServiceImpl(ServiceDetailRepository serviceDetailRepository,
                                    ServiceRepository serviceRepository,
                                    UserRepository userRepository) {
        this.serviceDetailRepository = serviceDetailRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> createServiceDetail(ServiceDetailRequestDTO requestDTO, Long userId) {
        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can create service details");
            }
        }

        Services service = serviceRepository.findById(requestDTO.getServiceId())
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus())
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + requestDTO.getServiceId()));

        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.setUuid(UUID.randomUUID().toString());
        serviceDetail.setHeading(requestDTO.getHeading());
        serviceDetail.setDetails(requestDTO.getDetails());
        serviceDetail.setDisplayOrder(requestDTO.getDisplayOrder());
        serviceDetail.setService(service);
        serviceDetail.setActive(requestDTO.isActive());
        serviceDetail.setDisplayStatus(requestDTO.isActive()); // Assuming active maps to displayStatus
        serviceDetail.setDeleteStatus(2);
        serviceDetail.setCreatedBy(createdBy);

        ServiceDetail savedServiceDetail = serviceDetailRepository.save(serviceDetail);
        return mapToResponseMap(savedServiceDetail);
    }

    @Override
    public Optional<Map<String, Object>> getServiceDetailById(Long id) {
        return serviceDetailRepository.findById(id)
                .filter(sd -> sd.getDeleteStatus() == 2 && sd.isActive() && sd.isDisplayStatus())
                .map(this::mapToResponseMap);
    }

    @Override
    public Optional<Map<String, Object>> getServiceDetailByUuid(String uuid) {
        return serviceDetailRepository.findByUuid(uuid)
                .filter(sd -> sd.getDeleteStatus() == 2 && sd.isActive() && sd.isDisplayStatus())
                .map(this::mapToResponseMap);
    }

    @Override
    public List<Map<String, Object>> getServiceDetailsByServiceId(Long serviceId) {
        return serviceDetailRepository.findByServiceId(serviceId)
                .stream()
                .filter(sd -> sd.getDeleteStatus() == 2 && sd.isActive() && sd.isDisplayStatus())
                .map(this::mapToResponseMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> updateServiceDetail(Long id, ServiceDetailRequestDTO requestDTO, Long userId) {
        ServiceDetail serviceDetail = serviceDetailRepository.findById(id)
                .filter(sd -> sd.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service detail not found with id: " + id));

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can update service details");
            }
        }

        Services service = serviceRepository.findById(requestDTO.getServiceId())
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus())
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + requestDTO.getServiceId()));

        serviceDetail.setHeading(requestDTO.getHeading());
        serviceDetail.setDetails(requestDTO.getDetails());
        serviceDetail.setDisplayOrder(requestDTO.getDisplayOrder());
        serviceDetail.setService(service);
        serviceDetail.setActive(requestDTO.isActive());
        serviceDetail.setDisplayStatus(requestDTO.isActive()); // Assuming active maps to displayStatus
        serviceDetail.setCreatedBy(createdBy);

        ServiceDetail updatedServiceDetail = serviceDetailRepository.save(serviceDetail);
        return mapToResponseMap(updatedServiceDetail);
    }

    @Override
    public void softDeleteServiceDetail(Long id, Long userId) {
        ServiceDetail serviceDetail = serviceDetailRepository.findById(id)
                .filter(sd -> sd.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service detail not found with id: " + id));

        User user = userRepository.findById(userId)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new IllegalArgumentException("Only ADMIN users can delete service details");
        }

        serviceDetail.setDeleteStatus(1);
        serviceDetail.setActive(false);
        serviceDetail.setDisplayStatus(false);
        serviceDetailRepository.save(serviceDetail);
    }

    private ServiceDetailResponseDTO mapToResponseDTO(ServiceDetail serviceDetail) {
        ServiceDetailResponseDTO dto = new ServiceDetailResponseDTO();
        dto.setId(serviceDetail.getId());
        dto.setUuid(serviceDetail.getUuid());
        dto.setHeading(serviceDetail.getHeading());
        dto.setDetails(serviceDetail.getDetails());
        dto.setDisplayOrder(serviceDetail.getDisplayOrder());
        dto.setServiceId(serviceDetail.getService().getId());
        dto.setActive(serviceDetail.isActive());
        dto.setCreatedAt(serviceDetail.getCreatedAt());
        dto.setUpdatedAt(serviceDetail.getUpdatedAt());
        dto.setCreatedById(serviceDetail.getCreatedBy() != null ? serviceDetail.getCreatedBy().getId() : null);
        return dto;
    }

    private Map<String, Object> mapToResponseMap(ServiceDetail serviceDetail) {
        ServiceDetailResponseDTO dto = mapToResponseDTO(serviceDetail);
        Map<String, Object> response = new HashMap<>();
        response.put("id", dto.getId());
        response.put("uuid", dto.getUuid());
        response.put("heading", dto.getHeading());
        response.put("details", dto.getDetails());
        response.put("displayOrder", dto.getDisplayOrder());
        response.put("serviceId", dto.getServiceId());
        response.put("active", dto.isActive());
        response.put("createdAt", dto.getCreatedAt());
        response.put("updatedAt", dto.getUpdatedAt());
        response.put("createdById", dto.getCreatedById());
        return response;
    }
}