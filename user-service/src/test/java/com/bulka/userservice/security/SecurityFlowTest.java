package com.bulka.userservice.security;

import com.bulka.userservice.config.CustomAuthenticationEntryPoint;
import com.bulka.userservice.config.JwtAuthenticationFilter;
import com.bulka.userservice.config.SecurityConfiguration;
import com.bulka.userservice.controller.AuthController;
import com.bulka.userservice.dto.request.LoginRequestDto;
import com.bulka.userservice.dto.request.RefreshTokenRequest;
import com.bulka.userservice.dto.request.RegistrationRequestDto;
import com.bulka.userservice.dto.response.TokenResponse;
import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.service.AuthService;
import com.bulka.userservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthController.class)
@Import({SecurityConfiguration.class, CustomAuthenticationEntryPoint.class, JwtAuthenticationFilter.class})
public class SecurityFlowTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private AuthService authService;

    @Test
    public void logout_shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/logout")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_shouldReturn401WithInvalidToken() throws Exception {
        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_shouldAllowAuthenticatedUser() throws Exception {
        String accessToken = "valid-access-token";
        Claims claims = Jwts.claims()
                .subject("1")
                .add("role", "USER")
                .build();

        when(jwtService.isValid(accessToken)).thenReturn(true);
        when(jwtService.getClaims(accessToken)).thenReturn(claims);

        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "refreshToken": "refresh-token"
                        }
                    """)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    public void security_shouldAllowLoginWithoutAuthentication() throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
                .email("email")
                .password("password")
                .build();
        TokenResponse response = TokenResponse.builder()
                .accessToken("valid-access-token")
                .refreshToken("refresh-token")
                .build();

        when(authService.login(loginRequestDto)).thenReturn(response);
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "email",
                                    "password": "password"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    public void security_shouldAllowRegisterWithoutAuthentication() throws Exception {
        UserResponse response = UserResponse.builder()
                .build();

        when(authService.register(any(RegistrationRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "email",
                                    "password": "password",
                                    "firstName": "firstName",
                                    "lastName": "lastName"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    public void security_shouldAllowRefreshWithoutAuthentication() throws Exception {
        TokenResponse response = TokenResponse.builder()
                .build();

        when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk());
    }

//    @Test
//    public void security_shouldAllowLoginWithoutAuthentication() throws Exception {
//        mockMvc.perform(post("/api/v1/auth/login"))
//                .andExpect(status().is2xxSuccessful());
//
//    }
//    @Test
//    public void security_shouldAllowRegisterWithoutAuthentication() throws Exception {
//        mockMvc.perform(post("/api/v1/auth/register"))
//                .andExpect(status().is2xxSuccessful());
//
//    }
//    @Test
//    public void security_shouldAllowRefreshWithoutAuthentication() throws Exception {
//        mockMvc.perform(post("/api/v1/auth/refresh"))
//                .andExpect(status().is2xxSuccessful());
//    }
//
//    @Test
//    public void security_shouldProtectLogoutWithoutAuthentication() throws Exception {
//        mockMvc.perform(post("/api/v1/auth/logout"))
//                .andExpect(status().isUnauthorized());
//    }
}
