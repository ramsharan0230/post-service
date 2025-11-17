package com.video.processing.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.video.processing.dtos.LoginRequest;
import com.video.processing.dtos.LoginResponse;
import com.video.processing.entities.AuthToken;
import com.video.processing.entities.User;
import com.video.processing.exceptions.ResourceNotFoundException;
import com.video.processing.repositories.AuthTokenRepository;
import com.video.processing.repositories.UserRepository;
import com.video.processing.utilities.PasswordUtil;

import jakarta.mail.MessagingException;

@Service
public class AuthService {
    @Value( "${app.base-url}" )
    private String baseUrl;

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final EmailService emailService;
    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(
            UserRepository userRepository,
            AuthTokenRepository authTokenRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
        this.emailService = emailService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(()->new ResourceNotFoundException("User not found with given email."));

        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        String hashedInputPassword = PasswordUtil.hashPassword(loginRequest.getPassword());

        logger.info("Hashed incoming password: "+ hashedInputPassword);
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

    public User findUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found with given email: "+email));
        try {
            Map<String, Object> variables = Map.of(
                    "firstName", user.getFirstName(),
                    "email", user.getEmail(),
                    "verificationLink", "https://yourdomain.com/verify?token=sdf08sdf7d8f6dfdf"
            );

            emailService.sendMail(
                    user.getEmail(),
                    "Welcome to Our Service!",
                    "welcome",
                    variables
            );
        } catch (MessagingException e) {
            logger.info("Failed to send welcome email to " + user.getEmail() + " | " + e.getMessage());
        }
        return user;
    }
}
