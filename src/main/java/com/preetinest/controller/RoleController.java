package com.preetinest.controller;

import com.preetinest.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "Endpoints for managing roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "Get all roles", description = "Retrieve a list of all active roles with relevant data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved roles"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<Map<String, Object>> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "Get role by UUID", description = "Retrieve a role by its unique UUID with relevant data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getRoleByUuid(@PathVariable String uuid) {
        Optional<Map<String, Object>> role = roleService.getRoleByUuid(uuid);
        return role.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new role", description = "Create a new role with the provided name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> savedRole = roleService.createRole(name);
        return ResponseEntity.status(201).body(savedRole);
    }

    @Operation(summary = "Update a role", description = "Update an existing role by UUID with a new name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable String uuid, @RequestParam String name) {
        try {
            Map<String, Object> updatedRole = roleService.updateRole(uuid, name);
            return ResponseEntity.ok(updatedRole);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a role", description = "Soft delete a role by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteRole(@PathVariable String uuid) {
        try {
            roleService.deleteRole(uuid);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}