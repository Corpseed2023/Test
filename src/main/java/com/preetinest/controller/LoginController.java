package com.preetinest.controller;

import com.preetinest.dto.request.LoginRequestDTO;
import com.preetinest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
@Tag(name = "Login Management", description = "API for user login in PreetiNest Global Connect")
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "User login", description = "Authenticates a user with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        Map<String, Object> response = userService.login(requestDTO);
        return ResponseEntity.ok(response);
    }
}