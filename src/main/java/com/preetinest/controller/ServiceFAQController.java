package com.preetinest.controller;

import com.preetinest.dto.request.ServiceFAQRequestDTO;
import com.preetinest.service.ServiceFAQService;
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
@RequestMapping("/api/service-faqs")
@Tag(name = "Service FAQ Management", description = "APIs for managing service FAQs in PreetiNest Global Connect")
public class ServiceFAQController {

    private final ServiceFAQService serviceFAQService;

    @Autowired
    public ServiceFAQController(ServiceFAQService serviceFAQService) {
        this.serviceFAQService = serviceFAQService;
    }

    @PostMapping
    @Operation(summary = "Create a new service FAQ", description = "Creates a new FAQ for a service; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service FAQ created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid FAQ data or userId"),
            @ApiResponse(responseCode = "404", description = "User or service not found")
    })
    public ResponseEntity<Map<String, Object>> createServiceFAQ(
            @Valid @RequestBody ServiceFAQRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = serviceFAQService.createServiceFAQ(requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service FAQ by ID", description = "Retrieves a service FAQ by its ID if not deleted, active, and displayed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service FAQ found"),
            @ApiResponse(responseCode = "404", description = "Service FAQ not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceFAQById(@PathVariable Long id) {
        return serviceFAQService.getServiceFAQById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    @Operation(summary = "Get service FAQ by UUID", description = "Retrieves a service FAQ by its UUID if not deleted, active, and displayed")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service FAQ found"),
            @ApiResponse(responseCode = "404", description = "Service FAQ not found")
    })
    public ResponseEntity<Map<String, Object>> getServiceFAQByUuid(@PathVariable String uuid) {
        return serviceFAQService.getServiceFAQByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{serviceId}")
    @Operation(summary = "Get service FAQs by service ID", description = "Retrieves all active, non-deleted, and displayed FAQs for a given service ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of service FAQs found"),
            @ApiResponse(responseCode = "404", description = "Service not found")
    })
    public ResponseEntity<List<Map<String, Object>>> getServiceFAQsByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok(serviceFAQService.getServiceFAQsByServiceId(serviceId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service FAQ", description = "Updates an existing service FAQ by ID; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service FAQ updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service FAQ or user not found"),
            @ApiResponse(responseCode = "400", description = "Invalid FAQ data or userId")
    })
    public ResponseEntity<Map<String, Object>> updateServiceFAQ(
            @PathVariable Long id,
            @Valid @RequestBody ServiceFAQRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = serviceFAQService.updateServiceFAQ(id, requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a service FAQ", description = "Marks a service FAQ as deleted by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service FAQ deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service FAQ or user not found")
    })
    public ResponseEntity<Void> softDeleteServiceFAQ(@PathVariable Long id, @RequestParam Long userId) {
        serviceFAQService.softDeleteServiceFAQ(id, userId);
        return ResponseEntity.noContent().build();
    }
}