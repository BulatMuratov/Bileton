package com.bulka.userservice.controller;

import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получение информации о пользователе")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserInfo(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(userId));
    }
}
