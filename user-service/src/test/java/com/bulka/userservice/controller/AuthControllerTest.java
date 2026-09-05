package com.bulka.userservice.controller;

import com.bulka.userservice.dto.request.LoginRequestDto;
import com.bulka.userservice.dto.request.RefreshTokenRequest;
import com.bulka.userservice.dto.request.RegistrationRequestDto;
import com.bulka.userservice.dto.response.TokenResponse;
import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.exception.auth.BadCredentialsException;
import com.bulka.userservice.exception.auth.InvalidRefreshTokenException;
import com.bulka.userservice.exception.auth.UserAlreadyExistsException;
import com.bulka.userservice.model.Role;
import com.bulka.userservice.model.UserStatus;
import com.bulka.userservice.service.AuthService;
import com.bulka.userservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void register_shouldReturnCreated() throws Exception {
        RegistrationRequestDto request = RegistrationRequestDto.builder()
                .email("murat@mail.ru")
                .password("1234")
                .firstName("Murat")
                .lastName("Murat")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        when(authService.register(request)).thenReturn(userResponse);

        mockMvc.perform(
                post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "murat@mail.ru",
                            "password": "1234",
                            "firstName": "Murat",
                            "lastName": "Murat"
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    public void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        RegistrationRequestDto request = RegistrationRequestDto.builder()
                .email("murat@mail.ru")
                .password("1234")
                .firstName("Murat")
                .lastName("Murat")
                .build();

        when(authService.register(request)).thenThrow(
                new UserAlreadyExistsException("User with this email already exists")
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "murat@mail.ru",
                            "password": "1234",
                            "firstName": "Murat",
                            "lastName": "Murat"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with this email already exists"))
                .andExpect(jsonPath("$.error").value("Conflict"));

        verify(authService).register(request);
    }

    @Test
    public void login_shouldReturnTokens() throws Exception {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("murat@mail.ru")
                .password("1234")
                .build();

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .build();

        when(authService.login(request)).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "murat@mail.ru",
                            "password": "1234"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(tokenResponse.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(tokenResponse.getRefreshToken()));
    }

    @Test
    public void login_shouldReturn401_whenEmailDoesNotExist() throws Exception {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("murat@mail.ru")
                .password("1234")
                .build();

        when(authService.login(request)).thenThrow(
                new BadCredentialsException("Invalid email or password")
        );

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "murat@mail.ru",
                            "password": "1234"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.error").value("Invalid credentials"));

        verify(authService).login(request);
    }

    @Test
    public void login_shouldReturn401_whenPasswordDoesNotMatch() throws Exception {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("murat@mail.ru")
                .password("1234")
                .build();

        when(authService.login(request)).thenThrow(
                new BadCredentialsException("Invalid email or password")
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "murat@mail.ru",
                            "password": "1234"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.error").value("Invalid credentials"));

        verify(authService).login(request);
    }

    @Test
    public void refresh_shouldReturnTokens_whenRefreshTokenIsValid() throws Exception {

        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .build();

        when(authService.refresh(any(RefreshTokenRequest.class)))
                .thenReturn(tokenResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "refreshToken": "old-refresh-token"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken")
                        .value("new-refresh-token"));

        verify(authService).refresh(any(RefreshTokenRequest.class));
    }

    @Test
    public void refresh_shouldReturn400_whenRefreshTokenIsInvalid() throws Exception {
        when(authService.refresh(any(RefreshTokenRequest.class))).thenThrow(
                new InvalidRefreshTokenException("Invalid refresh token")
        );

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "refreshToken": "old-refresh-token"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid refresh token"))
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));

        verify(authService).refresh(any(RefreshTokenRequest.class));
    }

    @Test
    public void refresh_shouldReturn400_whenUserNotFound() throws Exception {
        when(authService.refresh(any(RefreshTokenRequest.class))).thenThrow(
                new InvalidRefreshTokenException("User not found")
        );

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "refreshToken": "old-refresh-token"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.error").value("Invalid refresh token"));

        verify(authService).refresh(any(RefreshTokenRequest.class));
    }

    @Test
    void logout_shouldReturn204() throws Exception {

        doNothing().when(authService)
                .logout(any(RefreshTokenRequest.class));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "refreshToken": "refresh-token-123"
                    }
                    """))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(authService).logout(any(RefreshTokenRequest.class));
    }

}