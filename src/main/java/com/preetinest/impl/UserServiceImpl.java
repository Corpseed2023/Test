package com.preetinest.impl;

import com.preetinest.dto.request.UserRequestDTO;
import com.preetinest.dto.request.LoginRequestDTO;
import com.preetinest.entity.Role;
import com.preetinest.entity.User;
import com.preetinest.repository.RoleRepository;
import com.preetinest.repository.UserRepository;
import com.preetinest.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, Object> createUser(UserRequestDTO requestDTO, Long userId) {
        Optional<User> existingUser = userRepository.findByEmail(requestDTO.getEmail());
        if (existingUser.isPresent() && existingUser.get().getDeleteStatus() == 2 && existingUser.get().isEnable()) {
            throw new IllegalArgumentException("User with email " + requestDTO.getEmail() + " already exists");
        }

        Role role = roleRepository.findById(requestDTO.getRoleId())
                .filter(r -> r.getDeleteStatus() == 2 && r.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + requestDTO.getRoleId()));

        User createdBy = null;
        if (!"ADMIN".equalsIgnoreCase(role.getName())) {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required for non-ADMIN roles");
            }
            createdBy = userRepository.findById(userId)
                    .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        }

        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setName(requestDTO.getName());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setMobile(requestDTO.getMobile());
        user.setFacebook(requestDTO.getFacebook());
        user.setLinkedin(requestDTO.getLinkedin());
        user.setTwitter(requestDTO.getTwitter());
        user.setMetaTitle(requestDTO.getMetaTitle());
        user.setMetaKeyword(requestDTO.getMetaKeyword());
        user.setMetaDescription(requestDTO.getMetaDescription());
        user.setRole(role);
        user.setDeleteStatus(2);
        user.setEnable(true);
        user.setCreatedBy(createdBy);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public Optional<Map<String, Object>> getUserById(Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .map(this::mapToResponse);
    }

    @Override
    public Optional<Map<String, Object>> getUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .map(this::mapToResponse);
    }

    @Override
    public List<Map<String, Object>> getAllActiveUsers() {
        return userRepository.findAllActiveUsers()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Map<String, Object> updateUser(Long id, UserRequestDTO requestDTO) {
        User existingUser = userRepository.findById(id)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        Optional<User> existingEmailUser = userRepository.findByEmail(requestDTO.getEmail());
        if (existingEmailUser.isPresent() && existingEmailUser.get().getDeleteStatus() == 2 &&
                existingEmailUser.get().isEnable() && !existingEmailUser.get().getId().equals(id)) {
            throw new IllegalArgumentException("Email " + requestDTO.getEmail() + " is already in use by another user");
        }

        Role role = roleRepository.findById(requestDTO.getRoleId())
                .filter(r -> r.getDeleteStatus() == 2 && r.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + requestDTO.getRoleId()));

        existingUser.setName(requestDTO.getName());
        existingUser.setEmail(requestDTO.getEmail());
        existingUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        existingUser.setMobile(requestDTO.getMobile());
        existingUser.setFacebook(requestDTO.getFacebook());
        existingUser.setLinkedin(requestDTO.getLinkedin());
        existingUser.setTwitter(requestDTO.getTwitter());
        existingUser.setMetaTitle(requestDTO.getMetaTitle());
        existingUser.setMetaKeyword(requestDTO.getMetaKeyword());
        existingUser.setMetaDescription(requestDTO.getMetaDescription());
        existingUser.setRole(role);

        User updatedUser = userRepository.save(existingUser);
        return mapToResponse(updatedUser);
    }

    @Override
    public void softDeleteUser(Long id, Long userId) {
        User user = userRepository.findById(id)
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        user.setDeleteStatus(1);
        user.setEnable(false);
        userRepository.save(user);
        // Note: userId can be used for logging/auditing purposes
    }

    @Override
    public Map<String, Object> login(LoginRequestDTO requestDTO) {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .filter(u -> u.getDeleteStatus() == 2 && u.isEnable())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return mapToResponse(user);
    }

    private Map<String, Object> mapToResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("uuid", user.getUuid());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("mobile", user.getMobile());
        response.put("facebook", user.getFacebook());
        response.put("linkedin", user.getLinkedin());
        response.put("twitter", user.getTwitter());
        response.put("metaTitle", user.getMetaTitle());
        response.put("metaKeyword", user.getMetaKeyword());
        response.put("metaDescription", user.getMetaDescription());
        response.put("roleId", user.getRole() != null ? user.getRole().getId() : null);
        response.put("isEnable", user.isEnable());
        response.put("deleteStatus", user.getDeleteStatus());
        response.put("createdAt", user.getCreatedAt());
        response.put("updatedAt", user.getUpdatedAt());
        response.put("createdById", user.getCreatedBy() != null ? user.getCreatedBy().getId() : null);
        return response;
    }
}