package com.preetinest.controller;

import com.preetinest.dto.request.ServiceDetailRequestDTO;
import com.preetinest.service.ServiceDetailService;
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
@RequestMapping("/api/service-details")
@Tag(name = "Service Detail Management", description = "APIs for managing service details in PreetiNest Global Connect")
public class ServiceDetailController {

    private final ServiceDetailService serviceDetailService;

    @Autowired
    public ServiceDetailController(ServiceDetailService serviceDetailService) {
        this.serviceDetailService = serviceDetailService;
    }

    @PostMapping
    @Operation(summary = "Create a new service detail", description = "Creates a new service detail with the provided details; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service detail created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid service detail data or userId"),
            @ApiResponse(responseCode = "404", description = "User or service not found")
    })
    public ResponseEntity<Map<String, Object>> createServiceDetail(
            @Valid @RequestBody ServiceDetailRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = serviceDetailService.createServiceDetail(requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service detail by ID", description = "Retrieves a service detail by its ID if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service detail found"),
            @ApiResponse(responseCode = "404", description = "Service detail not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceDetailById(@PathVariable Long id) {
        return serviceDetailService.getServiceDetailById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    @Operation(summary = "Get service detail by UUID", description = "Retrieves a service detail by its UUID if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service detail found"),
            @ApiResponse(responseCode = "404", description = "Service detail not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceDetailByUuid(@PathVariable String uuid) {
        return serviceDetailService.getServiceDetailByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{serviceId}")
    @Operation(summary = "Get service details by service ID", description = "Retrieves all active service details for a given service ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of service details found"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<List<Map<String, Object>>> getServiceDetailsByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok(serviceDetailService.getServiceDetailsByServiceId(serviceId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service detail", description = "Updates an existing service detail by ID; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service detail updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service detail or user not found"),
            @ApiResponse(responseCode = "400", description = "Invalid service detail data or userId")
    })
    public ResponseEntity<Map<String, Object>> updateServiceDetail(
            @PathVariable Long id,
            @Valid @RequestBody ServiceDetailRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = serviceDetailService.updateServiceDetail(id, requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a service detail", description = "Marks a service detail as deleted by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service detail deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service detail or user not found")
    })
    public ResponseEntity<Void> softDeleteServiceDetail(@PathVariable Long id, @RequestParam Long userId) {
        serviceDetailService.softDeleteServiceDetail(id, userId);
        return ResponseEntity.noContent().build();
    }
}