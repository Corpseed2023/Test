package com.preetinest.controller;

import com.preetinest.dto.ServiceRequestDTO;
import com.preetinest.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Service Management", description = "APIs for managing services in PreetiNest Global Connect")
public class ServiceController {

    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    @Operation(summary = "Create a new service", description = "Creates a new service with the provided details; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid service data or userId"),
            @ApiResponse(responseCode = "404", description = "User or subcategory not found")
    })
    public ResponseEntity<Map<String, Object>> createService(
            @Valid @RequestBody ServiceRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = serviceService.createService(requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service by ID", description = "Retrieves a service by its ID if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service found"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceById(@PathVariable Long id) {
        return serviceService.getServiceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    @Operation(summary = "Get service by UUID", description = "Retrieves a service by its UUID if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service found"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceByUuid(@PathVariable String uuid) {
        return serviceService.getServiceByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get service by slug", description = "Retrieves a service by its slug if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service found"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceBySlug(@PathVariable String slug) {
        return serviceService.getServiceBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all active services", description = "Retrieves a list of all active and non-deleted services")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of active services")
    })
    public ResponseEntity<List<Map<String, Object>>> getAllActiveServices() {
        return ResponseEntity.ok(serviceService.getAllActiveServices());
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Get service with details by ID", description = "Retrieves a service and its associated service details by service ID if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service and details found"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceWithDetailsById(@PathVariable Long id) {
        return serviceService.getServiceWithDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service", description = "Updates an existing service by ID; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service or user not found"),
            @ApiResponse(responseCode = "400", description = "Invalid service data or userId")
    })
    public ResponseEntity<Map<String, Object>> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = serviceService.updateService(id, requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a service", description = "Marks a service as deleted by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service or user not found")
    })
    public ResponseEntity<Void> softDeleteService(@PathVariable Long id, @RequestParam Long userId) {
        serviceService.softDeleteService(id, userId);
        return ResponseEntity.noContent().build();
    }
}