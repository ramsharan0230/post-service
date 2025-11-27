package com.video.processing.services;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.video.processing.entities.PasswordReset;
import com.video.processing.repositories.PasswordResetRepository;

@Service
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;
    private final Logger logger = Logger.getLogger(PasswordResetService.class.getName());

    public PasswordResetService(PasswordResetRepository passwordResetRepository){
        this.passwordResetRepository = passwordResetRepository;
    }

    public void createPasswordReset(PasswordReset passwordReset){
        PasswordReset passwordResetCreated = this.passwordResetRepository.save(passwordReset);
        logger.info("password reset created: "+passwordReset.toString());
    }
}
