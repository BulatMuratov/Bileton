package com.bulka.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for user registration")
public class RegistrationRequestDto {
    @NotBlank
    @Email
    @Schema(
            description = "User email address",
            example = "bulat@example.com"
    )
    private String email;

    @NotBlank
    @Size(min=8, max=100)
    @Schema(
            description = "User password",
            example = "StrongPassword123",
            minLength = 8,
            maxLength = 100
    )
    private String password;

    @NotBlank
    @Size(max = 100)
    @Schema(
            description = "User first name",
            example = "Bulat",
            maxLength = 100
    )
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Schema(
            description = "User last name",
            example = "Muratov",
            maxLength = 100
    )
    private String lastName;
}
