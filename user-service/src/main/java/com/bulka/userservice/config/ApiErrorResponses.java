package com.bulka.userservice.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorResponses {
    boolean badRequest() default false;
    boolean invalidRefreshToken() default false;
    boolean unauthorized() default false;
    boolean invalidCredentials() default false;
    boolean conflict() default false;
}
