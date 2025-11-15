package com.video.processing.services;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.video.processing.dtos.LoginRequest;
import com.video.processing.dtos.LoginResponse;
import com.video.processing.entities.AuthToken;
import com.video.processing.entities.User;
import com.video.processing.repositories.AuthTokenRepository;
import com.video.processing.repositories.UserRepository;
import com.video.processing.utilities.PasswordUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(
            UserRepository userRepository,
            AuthTokenRepository authTokenRepository
    ) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {

        // 1. Fetch user
        User user = userRepository.findByEmail(loginRequest.getEmail());
        logger.info("User fetched: " + user);

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        // 2. Hash incoming password and compare
        String hashedInputPassword = PasswordUtil.hashPassword(loginRequest.getPassword());

        logger.info("Hashed incoming password: " + hashedInputPassword);
        logger.info("Stored password: " + user.getPassword());

        if (!hashedInputPassword.equals(user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // 3. Generate token
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        AuthToken token = new AuthToken(
                tokenValue,
                user.getId(),
                expiresAt
        );

        authTokenRepository.save(token);

        // 4. Build response
        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                tokenValue,
                expiresAt
        );
    }
}
