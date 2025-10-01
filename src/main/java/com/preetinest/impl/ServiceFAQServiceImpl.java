package com.preetinest.impl;

import com.preetinest.dto.request.ServiceFAQRequestDTO;
import com.preetinest.dto.response.ServiceFAQResponseDTO;
import com.preetinest.entity.ServiceFAQ;
import com.preetinest.entity.Services;
import com.preetinest.entity.User;
import com.preetinest.repository.ServiceFAQRepository;
import com.preetinest.repository.ServiceRepository;
import com.preetinest.repository.UserRepository;
import com.preetinest.service.ServiceFAQService;
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
public class ServiceFAQServiceImpl implements ServiceFAQService {

    private final ServiceFAQRepository serviceFAQRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public ServiceFAQServiceImpl(ServiceFAQRepository serviceFAQRepository,
                                 ServiceRepository serviceRepository,
                                 UserRepository userRepository) {
        this.serviceFAQRepository = serviceFAQRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> createServiceFAQ(ServiceFAQRequestDTO requestDTO, Long userId) {
        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can create service FAQs");
            }
        }

        Services service = serviceRepository.findById(requestDTO.getServiceId())
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus())
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + requestDTO.getServiceId()));

        ServiceFAQ serviceFAQ = new ServiceFAQ();
        serviceFAQ.setUuid(UUID.randomUUID().toString());
        serviceFAQ.setQuestion(requestDTO.getQuestion());
        serviceFAQ.setAnswer(requestDTO.getAnswer());
        serviceFAQ.setDisplayOrder(requestDTO.getDisplayOrder());
        serviceFAQ.setService(service);
        serviceFAQ.setActive(requestDTO.isActive());
        serviceFAQ.setDisplayStatus(requestDTO.isDisplayStatus());
        serviceFAQ.setDeleteStatus(2);
        serviceFAQ.setCreatedBy(createdBy);

        ServiceFAQ savedServiceFAQ = serviceFAQRepository.save(serviceFAQ);
        return mapToResponseMap(savedServiceFAQ);
    }

    @Override
    public Optional<Map<String, Object>> getServiceFAQById(Long id) {
        return serviceFAQRepository.findById(id)
                .filter(sf -> sf.getDeleteStatus() == 2 && sf.isActive() && sf.isDisplayStatus())
                .map(this::mapToResponseMap);
    }

    @Override
    public Optional<Map<String, Object>> getServiceFAQByUuid(String uuid) {
        return serviceFAQRepository.findByUuid(uuid)
                .filter(sf -> sf.getDeleteStatus() == 2 && sf.isActive() && sf.isDisplayStatus())
                .map(this::mapToResponseMap);
    }

    @Override
    public List<Map<String, Object>> getServiceFAQsByServiceId(Long serviceId) {
        return serviceFAQRepository.findByServiceId(serviceId)
                .stream()
                .filter(sf -> sf.getDeleteStatus() == 2 && sf.isActive() && sf.isDisplayStatus())
                .map(this::mapToResponseMap)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> updateServiceFAQ(Long id, ServiceFAQRequestDTO requestDTO, Long userId) {
        ServiceFAQ serviceFAQ = serviceFAQRepository.findById(id)
                .filter(sf -> sf.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service FAQ not found with id: " + id));

        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            if (!"ADMIN".equalsIgnoreCase(createdBy.getRole().getName())) {
                throw new IllegalArgumentException("Only ADMIN users can update service FAQs");
            }
        }

        Services service = serviceRepository.findById(requestDTO.getServiceId())
                .filter(s -> s.getDeleteStatus() == 2 && s.isActive() && s.isDisplayStatus())
                .orElseThrow(() -> new EntityNotFoundException("Service not found with id: " + requestDTO.getServiceId()));

        serviceFAQ.setQuestion(requestDTO.getQuestion());
        serviceFAQ.setAnswer(requestDTO.getAnswer());
        serviceFAQ.setDisplayOrder(requestDTO.getDisplayOrder());
        serviceFAQ.setService(service);
        serviceFAQ.setActive(requestDTO.isActive());
        serviceFAQ.setDisplayStatus(requestDTO.isDisplayStatus());
        serviceFAQ.setCreatedBy(createdBy);

        ServiceFAQ updatedServiceFAQ = serviceFAQRepository.save(serviceFAQ);
        return mapToResponseMap(updatedServiceFAQ);
    }

    @Override
    public void softDeleteServiceFAQ(Long id, Long userId) {
        ServiceFAQ serviceFAQ = serviceFAQRepository.findById(id)
                .filter(sf -> sf.getDeleteStatus() == 2)
                .orElseThrow(() -> new EntityNotFoundException("Service FAQ not found with id: " + id));

        User user = userRepository.findById(userId)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (!"ADMIN".equalsIgnoreCase(user.getRole().getName())) {
            throw new IllegalArgumentException("Only ADMIN users can delete service FAQs");
        }

        serviceFAQ.setDeleteStatus(1);
        serviceFAQ.setActive(false);
        serviceFAQ.setDisplayStatus(false);
        serviceFAQRepository.save(serviceFAQ);
    }

    private ServiceFAQResponseDTO mapToResponseDTO(ServiceFAQ serviceFAQ) {
        ServiceFAQResponseDTO dto = new ServiceFAQResponseDTO();
        dto.setId(serviceFAQ.getId());
        dto.setUuid(serviceFAQ.getUuid());
        dto.setQuestion(serviceFAQ.getQuestion());
        dto.setAnswer(serviceFAQ.getAnswer());
        dto.setDisplayOrder(serviceFAQ.getDisplayOrder());
        dto.setServiceId(serviceFAQ.getService().getId());
        dto.setActive(serviceFAQ.isActive());
        dto.setDisplayStatus(serviceFAQ.isDisplayStatus());
        dto.setCreatedAt(serviceFAQ.getCreatedAt());
        dto.setUpdatedAt(serviceFAQ.getUpdatedAt());
        dto.setCreatedById(serviceFAQ.getCreatedBy() != null ? serviceFAQ.getCreatedBy().getId() : null);
        return dto;
    }

    private Map<String, Object> mapToResponseMap(ServiceFAQ serviceFAQ) {
        ServiceFAQResponseDTO dto = mapToResponseDTO(serviceFAQ);
        Map<String, Object> response = new HashMap<>();
        response.put("id", dto.getId());
        response.put("uuid", dto.getUuid());
        response.put("question", dto.getQuestion());
        response.put("answer", dto.getAnswer());
        response.put("displayOrder", dto.getDisplayOrder());
        response.put("serviceId", dto.getServiceId());
        response.put("active", dto.isActive());
        response.put("displayStatus", dto.isDisplayStatus());
        response.put("createdAt", dto.getCreatedAt());
        response.put("updatedAt", dto.getUpdatedAt());
        response.put("createdById", dto.getCreatedById());
        return response;
    }
}