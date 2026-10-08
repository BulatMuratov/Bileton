package com.bulka.userservice.service;

import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.model.User;
import com.bulka.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUser(UUID userId) {
        return userRepository.findUserResponseById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));
    }
}
