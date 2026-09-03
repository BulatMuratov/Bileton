package com.bulka.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;
}
