package com.bulka.userservice.controller;

import com.bulka.userservice.config.ApiErrorResponses;
import com.bulka.userservice.dto.ErrorResponse;
import com.bulka.userservice.dto.request.LoginRequestDto;
import com.bulka.userservice.dto.request.RefreshTokenRequest;
import com.bulka.userservice.dto.request.RegistrationRequestDto;
import com.bulka.userservice.dto.response.TokenResponse;
import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authenticate controller")
@RestController
@RequestMapping("/api/v1/auth/")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @ApiErrorResponses(
            badRequest = true,
            conflict = true
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid RegistrationRequestDto request
    ) {
        UserResponse userResponse = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiErrorResponses(
            badRequest = true,
            invalidCredentials = true
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }

    @Operation(summary = "Обновление токенов")
    @ApiErrorResponses(
            invalidRefreshToken = true
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.refresh(request));
    }

    @Operation(summary = "Выход пользователя")
    @PostMapping("/logout")
    @ApiErrorResponses(
            invalidRefreshToken = true,
            unauthorized = true
    )
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @RequestBody @Valid RefreshTokenRequest request
    ) {
        authService.logout(request);
    }
}
