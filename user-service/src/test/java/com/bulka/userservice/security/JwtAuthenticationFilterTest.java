package com.bulka.userservice.security;


import com.bulka.userservice.config.JwtAuthenticationFilter;
import org.springframework.security.authentication.BadCredentialsException;
import com.bulka.userservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void jwtFilter_shouldContinueChainWhenAuthorizationHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void jwtFilter_shouldContinueChainWhenAuthorizationHeaderIsNotBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Basic abc");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void jwtFilter_shouldContinueChainWhenTokenIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "invalid-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.isValid(token)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        verify(jwtService).isValid(token);
        verify(jwtService, Mockito.never()).getClaims(anyString());
        verify(filterChain).doFilter(request, response);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void jwtFilter_shouldAuthenticateUserWhenTokenIsValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "valid-token";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = Jwts.claims()
                .subject("1")
                .add("role", "USER")
                .build();

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getClaims(token)).thenReturn(claims);

        filter.doFilter(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo("1");
        assertThat(authentication.getName()).isEqualTo("1");

        verify(jwtService).isValid(token);
        verify(jwtService).getClaims(token);
        verify(filterChain).doFilter(request, response);
    }


    @Test
    public void jwtFilter_shouldThrowBadCredentialsExceptionWhenClaimsAreInvalid() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "broken-token";
        request.addHeader("Authorization", "Bearer " + token);


        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getClaims(token))
                .thenThrow(
                        new MalformedJwtException("Malformed JWT")
                );

        assertThatThrownBy(() ->
                filter.doFilter(
                        request,
                        response,
                        filterChain
                )
        )
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid JWT token");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain, Mockito.never()).doFilter(request, response);
    }

    @Test
    void jwtFilter_shouldThrowBadCredentialsExceptionWhenClaimsThrowIllegalArgumentException() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = "broken-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.isValid(token)).thenReturn(true);
        when(jwtService.getClaims(token))
                .thenThrow(
                        new IllegalArgumentException("Invalid claims")
                );

        assertThatThrownBy(() ->
                filter.doFilter(
                        request,
                        response,
                        filterChain
                )
        )
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid JWT token");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain, Mockito.never())
                .doFilter(request, response);
    }
}
