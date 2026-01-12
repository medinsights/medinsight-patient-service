package com.medinsights.patient_service.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.UUID;

@Component
@Profile("!dev")
public class JwtContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // TEMPORARY (for local dev)
        String userId = request.getHeader("X-User-Id");

        if (userId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        request.setAttribute("userId", UUID.fromString(userId));
        filterChain.doFilter(request, response);
    }
}

