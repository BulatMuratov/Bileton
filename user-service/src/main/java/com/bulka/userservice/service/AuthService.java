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
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse register(RegistrationRequestDto registrationDto){
        if(userRepository.existsByEmail(registrationDto.getEmail())){
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        User user = User.builder()
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public TokenResponse login(LoginRequestDto loginDto){

        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.create(user.getId());


        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse refresh(RefreshTokenRequest refreshTokenDto){
        UUID userId = refreshTokenService.consume(refreshTokenDto.getRefreshToken());

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new InvalidRefreshTokenException("Invalid refresh token")
                );

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.create(userId);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(RefreshTokenRequest refreshTokenDto){
        refreshTokenService.revoke(refreshTokenDto.getRefreshToken());
    }

    private UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
