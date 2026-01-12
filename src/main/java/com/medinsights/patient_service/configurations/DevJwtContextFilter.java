package com.medinsights.patient_service.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Profile("dev")
public class DevJwtContextFilter extends OncePerRequestFilter {

    // Default test user ID for development
    private static final UUID DEFAULT_DEV_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Try to get userId from header first
        String userId = request.getHeader("X-User-Id");

        // If not provided, use default dev user ID
        UUID userUuid = (userId != null) ? UUID.fromString(userId) : DEFAULT_DEV_USER_ID;

        request.setAttribute("userId", userUuid);
        filterChain.doFilter(request, response);
    }
}
