package com.bulka.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
