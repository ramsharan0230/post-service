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
import com.video.processing.utilities.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;

@Service
public class AuthService {
    @Value( "${app.base-url}" )
    private String baseUrl;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthTokenService authTokenService;
    private final JwtTokenUtil jwtTokenUtil;


    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(
            UserRepository userRepository,
            EmailService emailService,
            ApplicationEventPublisher eventPublisher,
            AuthTokenService authTokenService,
            JwtTokenUtil jwtTokenUtil
    ) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.applicationEventPublisher = eventPublisher;
        this.authTokenService = authTokenService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String hashedInputPassword = PasswordUtil.hashPassword(loginRequest.getPassword());
        if (!hashedInputPassword.equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        String token = jwtTokenUtil.generateToken(
            user.getId(),
            user.getEmail(),
            user.getUsername()
        );
        
        AuthToken authToken = AuthToken.builder()
                .token(token)
                .userId(user.getId())
                .status(TokenType.VERIFIED)
                .expiresAt(LocalDateTime.now().plusHours(24)) 
                .createdAt(LocalDateTime.now())
                .isLoginToken(true)
                .build();
        
        this.authTokenService.createAuthToken(authToken);
        
        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                token,
                authToken.getExpiresAt()
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
