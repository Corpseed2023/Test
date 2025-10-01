package com.preetinest.impl;

import com.preetinest.dto.ServiceRequestDTO;
import com.preetinest.entity.ServiceDetail;
import com.preetinest.entity.Services;
import com.preetinest.entity.SubCategory;
import com.preetinest.entity.User;
import com.preetinest.repository.ServiceDetailRepository;
import com.preetinest.repository.ServiceRepository;
import com.preetinest.repository.SubCategoryRepository;
import com.preetinest.repository.UserRepository;
import com.preetinest.service.ServiceService;
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
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UserRepository userRepository;
    private final ServiceDetailRepository serviceDetailRepository;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository,
                              SubCategoryRepository subCategoryRepository,
                              UserRepository userRepository,
                              ServiceDetailRepository serviceDetailRepository) {
        this.serviceRepository = serviceRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.userRepository = userRepository;
        this.serviceDetailRepository = serviceDetailRepository;
    }

    @Override
    public Map<String, Object> createService(ServiceRequestDTO requestDTO, Long userId) {
        Optional<Services> existingService = serviceRepository.findBySlug(requestDTO.getSlug());
        if (existingService.isPresent() && existingService.get().getDeleteStatus() == 2) {
            throw new IllegalArgumentException("Service with slug " + requestDTO.getSlug() + " already exists");
        }

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can create services");
            }
        }

        SubCategory subCategory = subCategoryRepository.findById(requestDTO.getSubCategoryId())
                .filter(sc -> sc.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + requestDTO.getSubCategoryId()));

        Services service = new Services();
        service.setUuid(UUID.randomUUID().toString());
        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setSubCategory(subCategory);
        service.setIconUrl(requestDTO.getIconUrl());
        service.setImage(requestDTO.getImage());
        service.setMetaTitle(requestDTO.getMetaTitle());
        service.setMetaKeyword(requestDTO.getMetaKeyword());
        service.setMetaDescription(requestDTO.getMetaDescription());
        service.setSlug(requestDTO.getSlug());
        service.setActive(requestDTO.isActive());
        service.setDisplayStatus(requestDTO.isDisplayStatus());
        service.setShowOnHome(requestDTO.isShowOnHome());
        service.setDeleteStatus(2);
        service.setCreatedBy(createdBy);

        Services savedService = serviceRepository.save(service);
        return mapToResponseDTO(savedService);
    }

    @Override
    public Optional<Map<String, Object>> getServiceById(Long id) {
        return serviceRepository.findById(id)
                .filter(s -> s.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Optional<Map<String, Object>> getServiceByUuid(String uuid) {
        return serviceRepository.findByUuid(uuid)
                .filter(s -> s.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Optional<Map<String, Object>> getServiceBySlug(String slug) {
        return serviceRepository.findBySlug(slug)
                .filter(s -> s.getDeleteStatus() == 2)
                .map(this::mapToResponseDTO);
    }

    @Override
    public List<Map<String, Object>> getAllActiveServices() {
        return serviceRepository.findAllActiveServices()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> updateService(Long id, ServiceRequestDTO requestDTO, Long userId) {
        Services service = serviceRepository.findById(id)
                .filter(s -> s.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));

        Optional<Services> existingService = serviceRepository.findBySlug(requestDTO.getSlug());
        if (existingService.isPresent() && existingService.get().getDeleteStatus() == 2 &&
                !existingService.get().getId().equals(id)) {
            throw new IllegalArgumentException("Slug " + requestDTO.getSlug() + " is already in use by another service");
        }

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can update services");
            }
        }

        SubCategory subCategory = subCategoryRepository.findById(requestDTO.getSubCategoryId())
                .filter(sc -> sc.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + requestDTO.getSubCategoryId()));

        service.setName(requestDTO.getName());
        service.setDescription(requestDTO.getDescription());
        service.setSubCategory(subCategory);
        service.setIconUrl(requestDTO.getIconUrl());
        service.setImage(requestDTO.getImage());
        service.setMetaTitle(requestDTO.getMetaTitle());
        service.setMetaKeyword(requestDTO.getMetaKeyword());
        service.setMetaDescription(requestDTO.getMetaDescription());
        service.setSlug(requestDTO.getSlug());
        service.setActive(requestDTO.isActive());
        service.setDisplayStatus(requestDTO.isDisplayStatus());
        service.setShowOnHome(requestDTO.isShowOnHome());
        service.setCreatedBy(createdBy);

        Services updatedService = serviceRepository.save(service);
        return mapToResponseDTO(updatedService);
    }

    @Override
    public void softDeleteService(Long id, Long userId) {
        Services service = serviceRepository.findById(id)
                .filter(s -> s.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + id));

        User user = userRepository.findById(userId)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new IllegalArgumentException("Only ADMIN users can delete services");
        }

        service.setDeleteStatus(1);
        service.setActive(false);
        service.setDisplayStatus(false);
        service.setShowOnHome(false);
        serviceRepository.save(service);
    }

    private Map<String, Object> mapToResponseDTO(Services service) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", service.getId());
        response.put("uuid", service.getUuid());
        response.put("name", service.getName());
        response.put("description", service.getDescription());
        response.put("subCategoryId", service.getSubCategory().getId());
        response.put("iconUrl", service.getIconUrl());
        response.put("image", service.getImage());
        response.put("metaTitle", service.getMetaTitle());
        response.put("metaKeyword", service.getMetaKeyword());
        response.put("metaDescription", service.getMetaDescription());
        response.put("slug", service.getSlug());
        response.put("active", service.isActive());
        response.put("displayStatus", service.isDisplayStatus());
        response.put("showOnHome", service.isShowOnHome());
        response.put("createdAt", service.getCreatedAt());
        response.put("updatedAt", service.getUpdatedAt());
        response.put("createdById", service.getCreatedBy() != null ? service.getCreatedBy().getId() : null);
        return response;
    }

    @Override
    public Optional<Map<String, Object>> getServiceWithDetailsById(Long id) {
        Optional<Services> serviceOptional = serviceRepository.findById(id)
                .filter(s -> s.getDeleteStatus() == 2);
        if (serviceOptional.isEmpty()) {
            return Optional.empty();
        }

        Services service = serviceOptional.get();
        List<Map<String, Object>> serviceDetails = serviceDetailRepository.findByServiceId(id)
                .stream()
                .filter(sd -> sd.getDeleteStatus() == 2)
                .map(this::mapServiceDetailToResponseDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = mapToResponseDTO(service);
        response.put("serviceDetails", serviceDetails);
        return Optional.of(response);
    }

    private Map<String, Object> mapServiceDetailToResponseDTO(ServiceDetail serviceDetail) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", serviceDetail.getId());
        response.put("uuid", serviceDetail.getUuid());
        response.put("heading", serviceDetail.getHeading());
        response.put("overview", serviceDetail.getService());
        response.put("displayOrder", serviceDetail.getDisplayOrder());
        response.put("serviceId", serviceDetail.getService().getId());
        response.put("active", serviceDetail.isActive());
        response.put("createdAt", serviceDetail.getCreatedAt());
        response.put("updatedAt", serviceDetail.getUpdatedAt());
        response.put("createdById", serviceDetail.getCreatedBy() != null ? serviceDetail.getCreatedBy().getId() : null);
        return response;
    }




}