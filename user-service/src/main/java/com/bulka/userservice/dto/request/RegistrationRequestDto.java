package com.bulka.userservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
