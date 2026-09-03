package com.bulka.userservice.controller;

import com.bulka.userservice.dto.request.LoginRequestDto;
import com.bulka.userservice.dto.request.RegistrationRequestDto;
import com.bulka.userservice.dto.response.TokenResponse;
import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegistrationRequestDto request) {
        UserResponse userResponse = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody String refreshToken) {
        authService.logout(refreshToken);
    }
}
