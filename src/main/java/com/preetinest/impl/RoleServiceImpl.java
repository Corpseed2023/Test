package com.preetinest.impl;

import com.preetinest.entity.Role;
import com.preetinest.repository.RoleRepository;
import com.preetinest.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Map<String, Object>> getAllRoles() {
        return roleRepository.findAll().stream()
                .filter(role -> role.getDeleteStatus() == 2)
                .map(this::mapRoleToResponse)
                .toList();
    }

    @Override
    public Optional<Map<String, Object>> getRoleByUuid(String uuid) {
        return roleRepository.findByUuid(uuid)
                .filter(role -> role.getDeleteStatus() == 2)
                .map(this::mapRoleToResponse);
    }

    @Override
    public Map<String, Object> createRole(String name) {
        Role role = new Role();
        role.setName(name);
        role.setDeleteStatus(2);
        role.setEnable(true);
        role.setUuid(UUID.randomUUID().toString());
        Role savedRole = roleRepository.save(role);
        return mapRoleToResponse(savedRole);
    }

    @Override
    public Map<String, Object> updateRole(String uuid, String name) {
        Optional<Role> roleOptional = roleRepository.findByUuid(uuid);
        if (roleOptional.isEmpty() || roleOptional.get().getDeleteStatus() == 1) {
            throw new RuntimeException("Role not found with UUID: " + uuid);
        }
        Role role = roleOptional.get();
        role.setName(name);
        Role updatedRole = roleRepository.save(role);
        return mapRoleToResponse(updatedRole);
    }

    @Override
    public void deleteRole(String uuid) {
        Optional<Role> roleOptional = roleRepository.findByUuid(uuid);
        if (roleOptional.isEmpty() || roleOptional.get().getDeleteStatus() == 1) {
            throw new RuntimeException("Role not found with UUID: " + uuid);
        }
        Role role = roleOptional.get();
        role.setDeleteStatus(1);
        roleRepository.save(role);
    }

    private Map<String, Object> mapRoleToResponse(Role role) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", role.getId());
        response.put("name", role.getName());
        response.put("uuid", role.getUuid());
        response.put("isEnable", role.isEnable());
        response.put("deleteStatus", role.getDeleteStatus());
        response.put("createdAt", role.getCreatedAt());
        response.put("updatedAt", role.getUpdatedAt());
        return response;
    }
}