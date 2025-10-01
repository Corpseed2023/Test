package com.preetinest.service;

import com.preetinest.dto.request.LoginRequestDTO;
import com.preetinest.dto.request.UserRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Map<String, Object> createUser(UserRequestDTO requestDTO, Long userId);

    Optional<Map<String, Object>> getUserById(Long id);

    Optional<Map<String, Object>> getUserByUuid(String uuid);

    List<Map<String, Object>> getAllActiveUsers();

    Map<String, Object> updateUser(Long id, UserRequestDTO requestDTO);

    void softDeleteUser(Long id, Long userId);

    Map<String, Object> login(LoginRequestDTO requestDTO);
}