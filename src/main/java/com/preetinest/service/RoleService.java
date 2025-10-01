package com.preetinest.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RoleService {

    List<Map<String, Object>> getAllRoles();

    Optional<Map<String, Object>> getRoleByUuid(String uuid);

    Map<String, Object> createRole(String name);

    Map<String, Object> updateRole(String uuid, String name);

    void deleteRole(String uuid);
}