package com.video.processing.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.video.processing.dtos.LoginRequest;
import com.video.processing.dtos.LoginResponse;
import com.video.processing.entities.AuthToken;
import com.video.processing.entities.User;
import com.video.processing.exceptions.ResourceNotFoundException;
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
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()->new ResourceNotFoundException("User not found with given email."));

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        // 2. Hash incoming password and compare
        String hashedInputPassword = PasswordUtil.hashPassword(loginRequest.getPassword());

        logger.info("Hashed incoming password: " + hashedInputPassword);

        if (!hashedInputPassword.equals(user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        AuthToken token = new AuthToken(
                tokenValue,
                user.getId(),
                expiresAt
        );

        authTokenRepository.save(token);

        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                tokenValue,
                expiresAt
        );
    }

    public Optional<User> findUserByEmail(String email){
        logger.info("get email: "+email);
        return userRepository.findByEmail(email);
    }
}
