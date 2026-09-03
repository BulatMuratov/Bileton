package com.bulka.userservice.dto.response;

import com.bulka.userservice.model.Role;
import com.bulka.userservice.model.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private UserStatus status;
}
