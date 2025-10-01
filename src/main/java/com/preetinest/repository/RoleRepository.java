package com.preetinest.repository;

import com.preetinest.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByUuid(String uuid);
    Optional<Role> findByName(String name);
}