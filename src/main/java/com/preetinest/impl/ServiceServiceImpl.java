package com.preetinest.impl;

import com.preetinest.dto.ServiceRequestDTO;
import com.preetinest.dto.response.ServiceDetailResponseDTO;
import com.preetinest.dto.response.ServiceResponseDTO;
import com.preetinest.entity.Services;
import com.preetinest.entity.ServiceDetail;
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
        return mapToResponseMap(savedService);
    }

    @Override
    public Optional<Map<String, Object>> getServiceById(Long id) {
        return serviceRepository.findById(id)
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus())
                .map(this::mapToResponseMap);
    }

    @Override
    public Optional<Map<String, Object>> getServiceByUuid(String uuid) {
        return serviceRepository.findByUuid(uuid)
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus())
                .map(this::mapToResponseMap);
    }

    @Override
    public Optional<Map<String, Object>> getServiceBySlug(String slug) {
        return serviceRepository.findBySlug(slug)
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus())
                .map(this::mapToResponseMap);
    }

    @Override
    public List<Map<String, Object>> getAllActiveServices() {
        return serviceRepository.findAllActiveServices()
                .stream()
                .filter(s -> s.isActive() && s.isDisplayStatus())
                .map(this::mapToResponseMap)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Map<String, Object>> getServiceWithDetailsById(Long id) {
        Optional<Services> serviceOptional = serviceRepository.findById(id)
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus());
        if (serviceOptional.isEmpty()) {
            return Optional.empty();
        }

        Services service = serviceOptional.get();
        List<ServiceDetailResponseDTO> serviceDetails = serviceDetailRepository.findByServiceId(id)
                .stream()
                .filter(sd -> sd.getDeleteStatus() == 2 && sd.isActive() && sd.isDisplayStatus())
                .map(this::mapToServiceDetailResponseDTO)
                .collect(Collectors.toList());

        ServiceResponseDTO serviceResponseDTO = mapToServiceResponseDTO(service);
        serviceResponseDTO.setServiceDetails(serviceDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("id", serviceResponseDTO.getId());
        response.put("uuid", serviceResponseDTO.getUuid());
        response.put("name", serviceResponseDTO.getName());
        response.put("description", serviceResponseDTO.getDescription());
        response.put("subCategoryId", serviceResponseDTO.getSubCategoryId());
        response.put("subCategoryName", serviceResponseDTO.getSubCategoryName());
        response.put("categoryId", serviceResponseDTO.getCategoryId());
        response.put("categoryName", serviceResponseDTO.getCategoryName());
        response.put("iconUrl", serviceResponseDTO.getIconUrl());
        response.put("image", serviceResponseDTO.getImage());
        response.put("metaTitle", serviceResponseDTO.getMetaTitle());
        response.put("metaKeyword", serviceResponseDTO.getMetaKeyword());
        response.put("metaDescription", serviceResponseDTO.getMetaDescription());
        response.put("slug", serviceResponseDTO.getSlug());
        response.put("active", serviceResponseDTO.isActive());
        response.put("displayStatus", serviceResponseDTO.isDisplayStatus());
        response.put("showOnHome", serviceResponseDTO.isShowOnHome());
        response.put("createdAt", serviceResponseDTO.getCreatedAt());
        response.put("updatedAt", serviceResponseDTO.getUpdatedAt());
        response.put("createdById", serviceResponseDTO.getCreatedById());
        response.put("serviceDetails", serviceResponseDTO.getServiceDetails());
        return Optional.of(response);
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
        return mapToResponseMap(updatedService);
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

    private ServiceResponseDTO mapToServiceResponseDTO(Services service) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setId(service.getId());
        dto.setUuid(service.getUuid());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setSubCategoryId(service.getSubCategory().getId());
        dto.setSubCategoryName(service.getSubCategory().getName());
        dto.setCategoryId(service.getSubCategory().getCategory().getId());
        dto.setCategoryName(service.getSubCategory().getCategory().getName());
        dto.setIconUrl(service.getIconUrl());
        dto.setImage(service.getImage());
        dto.setMetaTitle(service.getMetaTitle());
        dto.setMetaKeyword(service.getMetaKeyword());
        dto.setMetaDescription(service.getMetaDescription());
        dto.setSlug(service.getSlug());
        dto.setActive(service.isActive());
        dto.setDisplayStatus(service.isDisplayStatus());
        dto.setShowOnHome(service.isShowOnHome());
        dto.setCreatedAt(service.getCreatedAt());
        dto.setUpdatedAt(service.getUpdatedAt());
        dto.setCreatedById(service.getCreatedBy() != null ? service.getCreatedBy().getId() : null);
        return dto;
    }

    private ServiceDetailResponseDTO mapToServiceDetailResponseDTO(ServiceDetail serviceDetail) {
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

    private Map<String, Object> mapToResponseMap(Services service) {
        ServiceResponseDTO dto = mapToServiceResponseDTO(service);
        Map<String, Object> response = new HashMap<>();
        response.put("id", dto.getId());
        response.put("uuid", dto.getUuid());
        response.put("name", dto.getName());
        response.put("description", dto.getDescription());
        response.put("subCategoryId", dto.getSubCategoryId());
        response.put("subCategoryName", dto.getSubCategoryName());
        response.put("categoryId", dto.getCategoryId());
        response.put("categoryName", dto.getCategoryName());
        response.put("iconUrl", dto.getIconUrl());
        response.put("image", dto.getImage());
        response.put("metaTitle", dto.getMetaTitle());
        response.put("metaKeyword", dto.getMetaKeyword());
        response.put("metaDescription", dto.getMetaDescription());
        response.put("slug", dto.getSlug());
        response.put("active", dto.isActive());
        response.put("displayStatus", dto.isDisplayStatus());
        response.put("showOnHome", dto.isShowOnHome());
        response.put("createdAt", dto.getCreatedAt());
        response.put("updatedAt", dto.getUpdatedAt());
        response.put("createdById", dto.getCreatedById());
        return response;
    }
}