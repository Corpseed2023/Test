package com.preetinest.impl;

import com.preetinest.dto.request.ServiceDetailRequestDTO;
import com.preetinest.entity.ServiceDetail;
import com.preetinest.entity.Services;
import com.preetinest.entity.User;
import com.preetinest.repository.ServiceDetailRepository;
import com.preetinest.repository.ServiceRepository;
import com.preetinest.repository.UserRepository;
import com.preetinest.service.ServiceDetailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


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
                .filter(s -> s.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + requestDTO.getServiceId()));

        ServiceDetail serviceDetail = new ServiceDetail();
        serviceDetail.setUuid(UUID.randomUUID().toString());
        serviceDetail.setHeading(requestDTO.getHeading());
        serviceDetail.setDetails(requestDTO.getDetails());
        serviceDetail.setDisplayOrder(requestDTO.getDisplayOrder());
        serviceDetail.setService(service);
        serviceDetail.setActive(requestDTO.isActive());
        serviceDetail.setDeleteStatus(2);
        serviceDetail.setCreatedBy(createdBy);

        ServiceDetail savedServiceDetail = serviceDetailRepository.save(serviceDetail);
        return mapToResponseDTO(savedServiceDetail);
    }

    @Override
    public Optional<Map<String, Object>> getServiceDetailById(Long id) {
        return serviceDetailRepository.findById(id)
                .filter(sd -> sd.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Optional<Map<String, Object>> getServiceDetailByUuid(String uuid) {
        return serviceDetailRepository.findByUuid(uuid)
                .filter(sd -> sd.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public List<Map<String, Object>> getServiceDetailsByServiceId(Long serviceId) {
        return serviceDetailRepository.findByServiceId(serviceId)
                .stream()
                .filter(sd -> sd.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO)
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
                .filter(s -> s.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + requestDTO.getServiceId()));

        serviceDetail.setHeading(requestDTO.getHeading());
        serviceDetail.setDetails(requestDTO.getDetails());
        serviceDetail.setDisplayOrder(requestDTO.getDisplayOrder());
        serviceDetail.setService(service);
        serviceDetail.setActive(requestDTO.isActive());
        serviceDetail.setCreatedBy(createdBy);

        ServiceDetail updatedServiceDetail = serviceDetailRepository.save(serviceDetail);
        return mapToResponseDTO(updatedServiceDetail);
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
        serviceDetailRepository.save(serviceDetail);
    }

    private Map<String, Object> mapToResponseDTO(ServiceDetail serviceDetail) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", serviceDetail.getId());
        response.put("uuid", serviceDetail.getUuid());
        response.put("heading", serviceDetail.getHeading());
        response.put("details", serviceDetail.getDetails());
        response.put("displayOrder", serviceDetail.getDisplayOrder());
        response.put("serviceId", serviceDetail.getService().getId());
        response.put("active", serviceDetail.isActive());
        response.put("createdAt", serviceDetail.getCreatedAt());
        response.put("updatedAt", serviceDetail.getUpdatedAt());
        response.put("createdById", serviceDetail.getCreatedBy() != null ? serviceDetail.getCreatedBy().getId() : null);
        return response;
    }
}