package com.bulka.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserInfoResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String status;
}
