package com.preetinest.controller;

import com.preetinest.dto.SubCategoryRequestDTO;
import com.preetinest.service.SubCategoryService;
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
@RequestMapping("/api/subcategories")
@Tag(name = "SubCategory Management", description = "APIs for managing subcategories in PreetiNest Global Connect")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @Autowired
    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new subcategory", description = "Creates a new subcategory with the provided details; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategory created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid subcategory data or userId"),
            @ApiResponse(responseCode = "404", description = "User or category not found")
    })
    public ResponseEntity<Map<String, Object>> createSubCategory(
            @Valid @RequestBody SubCategoryRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = subCategoryService.createSubCategory(requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subcategory by ID", description = "Retrieves a subcategory by its ID if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategory found"),
            @ApiResponse(responseCode = "404", description = "Subcategory not found")
    })
    public ResponseEntity<Map<String, Object>> getSubCategoryById(@PathVariable Long id) {
        return subCategoryService.getSubCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/uuid/{uuid}")
    @Operation(summary = "Get subcategory by UUID", description = "Retrieves a subcategory by its UUID if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategory found"),
            @ApiResponse(responseCode = "404", description = "Subcategory not found")
    })
    public ResponseEntity<Map<String, Object>> getSubCategoryByUuid(@PathVariable String uuid) {
        return subCategoryService.getSubCategoryByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get subcategory by slug", description = "Retrieves a subcategory by its slug if not deleted")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategory found"),
            @ApiResponse(responseCode = "404", description = "Subcategory not found")
    })
    public ResponseEntity<Map<String, Object>> getSubCategoryBySlug(@PathVariable String slug) {
        return subCategoryService.getSubCategoryBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all active subcategories", description = "Retrieves a list of all active and non-deleted subcategories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of active subcategories")
    })
    public ResponseEntity<List<Map<String, Object>>> getAllActiveSubCategories() {
        return ResponseEntity.ok(subCategoryService.getAllActiveSubCategories());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a subcategory", description = "Updates an existing subcategory by ID; userId is optional for ADMIN role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategory updated successfully"),
            @ApiResponse(responseCode = "404", description = "Subcategory or user not found"),
            @ApiResponse(responseCode = "400", description = "Invalid subcategory data or userId")
    })
    public ResponseEntity<Map<String, Object>> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequestDTO requestDTO,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> response = subCategoryService.updateSubCategory(id, requestDTO, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a subcategory", description = "Marks a subcategory as deleted by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subcategory deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subcategory or user not found")
    })
    public ResponseEntity<Void> softDeleteSubCategory(@PathVariable Long id, @RequestParam Long userId) {
        subCategoryService.softDeleteSubCategory(id, userId);
        return ResponseEntity.noContent().build();
    }
}