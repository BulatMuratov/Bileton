package com.bulka.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for refreshing access token")
public class RefreshTokenRequest {

    @NotBlank
    @Schema(
            description = "Refresh token issued during authentication",
            example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;
}
