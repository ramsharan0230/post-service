package com.video.processing.utilities;

import com.video.processing.entities.AuthToken;
import com.video.processing.entities.User;
import com.video.processing.repositories.AuthTokenRepository;
import com.video.processing.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class AuthFilter extends OncePerRequestFilter {

    private final AuthTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final Logger logger = Logger.getLogger(AuthFilter.class.getName());

    public AuthFilter(AuthTokenRepository tokenRepo, UserRepository userRepo) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("Auth Filter");

        String path = request.getRequestURI();

        // Skip auth & user registration endpoints
        if (path.startsWith("/api/auth") || path.startsWith("/api/users") || path.startsWith("/api/posts")) {
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

        // 🔥 Load user
        User user = userRepo.findById(authToken.getUserId()).orElse(null);

        if (user == null) {
            unauthorized(response, "User not found");
            return;
        }

        // 🔥 Make user available to controller
        request.setAttribute("currentUser", user);

        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(message);
    }
}
