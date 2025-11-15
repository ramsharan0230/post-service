package com.video.processing.utilities;

import com.video.processing.entities.AuthToken;
import com.video.processing.repositories.AuthTokenRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class AuthFilter extends OncePerRequestFilter {

    private final AuthTokenRepository tokenRepo;
    private final Logger logger = Logger.getLogger(AuthFilter.class.getName());

    public AuthFilter(AuthTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
                                        logger.info("Auth Filter");
        String path = request.getRequestURI();

        // Exclude authentication endpoints
        if (path.startsWith("/api/auth") || path.startsWith("/api/users")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("X-Auth-Token");

        if (token == null || token.isEmpty()) {
            unauthorized(response, "Missing token");
            return;
        }

        AuthToken authToken = tokenRepo.findById(token).orElse(null);

        if (authToken == null) {
            unauthorized(response, "Invalid token");
            return;
        }

        if (authToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            unauthorized(response, "Token expired");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(message);
    }
}