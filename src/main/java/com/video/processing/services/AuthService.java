package com.video.processing.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.video.processing.enums.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.video.processing.dtos.LoginRequest;
import com.video.processing.dtos.LoginResponse;
import com.video.processing.entities.AuthToken;
import com.video.processing.entities.User;
import com.video.processing.events.PasswordResetEvent;
import com.video.processing.exceptions.ResourceNotFoundException;
import com.video.processing.repositories.UserRepository;
import com.video.processing.utilities.PasswordUtil;

import jakarta.mail.MessagingException;

@Service
public class AuthService {
    @Value( "${app.base-url}" )
    private String baseUrl;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthTokenService authTokenService;

    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(
            UserRepository userRepository,
            EmailService emailService,
            ApplicationEventPublisher eventPublisher,
            AuthTokenService authTokenService
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.applicationEventPublisher = eventPublisher;
        this.authTokenService = authTokenService;
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
        AuthToken authToken = AuthToken.builder()
                .token(tokenValue)
                .userId(user.getId())
                .status(TokenType.VERIFIED)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .isLoginToken(true)
                .build();

        AuthToken authTokenCreated = this.authTokenService.createAuthToken(authToken);
        System.out.println("Auth_token; "+authTokenCreated);

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
            UUID randomUuid = UUID.randomUUID();
            Map<String, Object> variables = Map.of(
                    "firstName", user.getFirstName(),
                    "email", user.getEmail(),
                    "verificationLink", baseUrl+"/api/auth/verify?token="+randomUuid
            );

            emailService.sendMail(
                    user.getEmail(),
                    "Welcome to Our Service!",
                    "welcome",
                    variables
            );
            AuthToken authToken = new AuthToken();
            authToken.setToken(randomUuid.toString());
            authToken.setUserId(user.getId());
            authToken.setStatus(TokenType.PENDING);
            authToken.setLoginToken(false);

            applicationEventPublisher.publishEvent(new PasswordResetEvent(user));
//            applicationEventPublisher.publishEvent(new AuthPasswordResetToken(authToken));
        } catch (MessagingException e) {
            logger.info("Failed to send welcome email to " + user.getEmail() + " | " + e.getMessage());
        }
        return user;
    }

    public User verifyPasswordResetToken(String token){
        this.logger.info("token: "+token);
        return null;
    }
}
