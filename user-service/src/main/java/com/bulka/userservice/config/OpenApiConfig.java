package com.bulka.userservice.config;

import com.bulka.userservice.dto.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bileton User Service API")
                        .description("API for auth")
                        .version("1.0")
                )
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                        .addSchemas(
                                "ErrorResponse",
                                ModelConverters.getInstance()
                                        .resolveAsResolvedSchema(
                                                new AnnotatedType(ErrorResponse.class)
                                        )
                                        .schema
                        )
                );
    }

    @Bean
    public OperationCustomizer apiErrorResponsesCustomizer() {
        return (operation, handlerMethod) -> {

            // 500 — глобальный
            operation.getResponses().addApiResponse(
                    "500",
                    errorResponse(
                            500,
                            "SERVER ERROR",
                            "Internal server error"
                    )
            );

            ApiErrorResponses annotation =
                    handlerMethod.getMethodAnnotation(ApiErrorResponses.class);

            if (annotation == null) {
                return operation;
            }

            if (annotation.badRequest()) {
                operation.getResponses().addApiResponse(
                        "400",
                        errorResponse(
                                400,
                                "Validation error.",
                                "firstName: не должно быть пустым, lastName: не должно быть пустым," +
                                        " email: должно иметь формат адреса электронной почты," +
                                        " password: размер должен находиться в диапазоне от 8 до 100"
                        )
                );
            }
            if (annotation.invalidRefreshToken()) {
                operation.getResponses().addApiResponse(
                        "400",
                        errorResponse(
                                400,
                                "Invalid refresh token",
                                "Invalid refresh token"
                        )
                );
            }
            if (annotation.unauthorized()) {
                operation.getResponses().addApiResponse(
                        "401",
                        errorResponse(
                                401,
                                "Authentication error.",
                                "Authentication is required"
                        )
                );
            }
            if (annotation.invalidCredentials()) {
                operation.getResponses().addApiResponse(
                        "401",
                        errorResponse(
                                401,
                                "Invalid credentials",
                                "Invalid email or password"
                                )
                );
            }

            if (annotation.conflict()) {
                operation.getResponses().addApiResponse(
                        "409",
                        errorResponse(
                                409,
                                "Conflict",
                                "User with this email already exists"
                        )
                );
            }

            return operation;
        };
    }

    private ApiResponse errorResponse(
            int status,
            String error,
            String message
    ) {
        return new ApiResponse()
                .description(error)
                .content(new Content()
                        .addMediaType(
                                "application/json",
                                new MediaType()
                                        .schema(new Schema<>()
                                                .$ref("#/components/schemas/ErrorResponse"))
                                        .example(Map.of(
                                                "status", status,
                                                "message", message,
                                                "error", error
                                        ))
                        )
                );
    }

}
