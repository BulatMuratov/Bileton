package com.bulka.userservice.service;

import com.bulka.userservice.dto.request.LoginRequestDto;
import com.bulka.userservice.dto.request.RefreshTokenRequest;
import com.bulka.userservice.dto.request.RegistrationRequestDto;
import com.bulka.userservice.dto.response.TokenResponse;
import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.exception.auth.BadCredentialsException;
import com.bulka.userservice.exception.auth.InvalidRefreshTokenException;
import com.bulka.userservice.exception.auth.UserAlreadyExistsException;
import com.bulka.userservice.model.Role;
import com.bulka.userservice.model.User;
import com.bulka.userservice.model.UserStatus;
import com.bulka.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void register_shouldCreateUserSuccessfully(){
        RegistrationRequestDto request = RegistrationRequestDto.builder()
                .email("muratov@mail.ru")
                .password("12345")
                .firstName("Bulat")
                .lastName("Muratov")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .password("HASH")
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("HASH");
        when(userRepository.save(Mockito.any())).thenReturn(user);

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(response.getId(), user.getId());
        assertEquals(response.getEmail(), user.getEmail());
        assertEquals(response.getFirstName(), user.getFirstName());
        assertEquals(response.getLastName(), user.getLastName());
        assertEquals(Role.USER, response.getRole());
        assertEquals(UserStatus.ACTIVE, response.getStatus());

        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(Mockito.any());
    }

    @Test
    public void register_shouldThrowException_whenEmailAlreadyExist() {
        RegistrationRequestDto request = RegistrationRequestDto.builder()
                .email("muratov@mail.ru")
                .password("12345")
                .firstName("Bulat")
                .lastName("Muratov")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, Mockito.never()).save(Mockito.any());
        verify(passwordEncoder, Mockito.never()).encode(Mockito.any());

    }

    @Test
    public void register_shouldSaveCorrectUser() {
        RegistrationRequestDto request = RegistrationRequestDto.builder()
                .email("muratov@mail.ru")
                .password("12345")
                .firstName("Bulat")
                .lastName("Muratov")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("HASHED_PASSWORD");

        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals("HASHED_PASSWORD", savedUser.getPassword());
        assertEquals(request.getFirstName(), savedUser.getFirstName());
        assertEquals(request.getLastName(), savedUser.getLastName());
        assertEquals(Role.USER, savedUser.getRole());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    }

    @Test
    public void login_shouldLoginUserSuccessfully(){
        LoginRequestDto request = LoginRequestDto.builder()
                .email("murat@mail.ru")
                .password("12345")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .password("HASH")
                .firstName("Bulat")
                .lastName("Muratov")
                .status(UserStatus.ACTIVE)
                .role(Role.USER)
                .build();
        String accessToken = "access";
        String refreshToken = "refresh";

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(refreshTokenService.create(user.getId())).thenReturn(refreshToken);

        TokenResponse tokenResponse = authService.login(request);

        assertNotNull(tokenResponse);
        assertEquals(accessToken, tokenResponse.getAccessToken());
        assertEquals(refreshToken, tokenResponse.getRefreshToken());
    }

    @Test
    public void login_shouldThrowException_whenEmailDoesNotExist() {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("murat@mail.ru")
                .password("12345")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .password("HASH")
                .firstName("Bulat")
                .lastName("Muratov")
                .status(UserStatus.ACTIVE)
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid email or password",  exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder, Mockito.never()).matches(request.getPassword(), user.getPassword());
        verify(jwtService, Mockito.never()).generateAccessToken(Mockito.any());
        verify(refreshTokenService, Mockito.never()).create(Mockito.any());
    }

    @Test
    public void login_shouldThrowException_whenPasswordDoesNotEquals() {
        LoginRequestDto request = LoginRequestDto.builder()
                .email("murat@mail.ru")
                .password("12345")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .password("HASH")
                .firstName("Bulat")
                .lastName("Muratov")
                .status(UserStatus.ACTIVE)
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Invalid email or password",  exception.getMessage());
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtService, Mockito.never()).generateAccessToken(Mockito.any());
        verify(refreshTokenService, Mockito.never()).create(Mockito.any());
    }

    @Test
    public void refresh_shouldUpdateAccessAndRefreshTokens() {
        RefreshTokenRequest refreshTokenDto = RefreshTokenRequest.builder()
                .refreshToken("refreshToken1")
                .build();

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();

        String accessToken = "access";
        String refreshToken = "refreshToken2";

        when(refreshTokenService.consume(refreshTokenDto.getRefreshToken())).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(refreshTokenService.create(userId)).thenReturn(refreshToken);

        TokenResponse tokenResponse = authService.refresh(refreshTokenDto);

        assertNotNull(tokenResponse);
        assertEquals(accessToken, tokenResponse.getAccessToken());
        assertEquals(refreshToken, tokenResponse.getRefreshToken());

        verify(refreshTokenService).consume(refreshTokenDto.getRefreshToken());
        verify(userRepository).findById(userId);
        verify(jwtService).generateAccessToken(user);
        verify(refreshTokenService).create(userId);
    }

    @Test
    public void refresh_shouldThrowException_whenUserNotFound() {
        RefreshTokenRequest refreshTokenDto = RefreshTokenRequest.builder()
                .refreshToken("refreshToken1")
                .build();

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();


        when(refreshTokenService.consume(refreshTokenDto.getRefreshToken())).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        InvalidRefreshTokenException exception = assertThrows(
                InvalidRefreshTokenException.class,
                () -> authService.refresh(refreshTokenDto)
        );

        assertEquals("User not found", exception.getMessage());

        verify(refreshTokenService).consume(refreshTokenDto.getRefreshToken());
        verify(userRepository).findById(userId);
        verify(jwtService, Mockito.never()).generateAccessToken(user);
        verify(refreshTokenService, Mockito.never()).create(userId);
    }

    @Test
    void logout_shouldRevokeRefreshToken() {

        String refreshToken = "refresh-token";

        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();

        authService.logout(request);

        verify(refreshTokenService).revoke(refreshToken);
    }

}