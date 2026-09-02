package com.bulka.userservice.dto.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {
    private String email;
    private String password;
}
