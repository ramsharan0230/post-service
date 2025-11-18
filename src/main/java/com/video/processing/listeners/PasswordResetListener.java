package com.video.processing.listeners;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.video.processing.entities.PasswordReset;
import com.video.processing.events.PasswordResetEvent;
import com.video.processing.repositories.PasswordResetRepository;
import com.video.processing.services.PasswordResetService;

@Component
public class PasswordResetListener {
    private final PasswordResetService passwordResetService;
    private final Logger logger = Logger.getLogger(PasswordResetListener.class.getName());
    private final PasswordResetRepository passwordResetRepository;

    public PasswordResetListener(
        PasswordResetService passwordResetService,
        PasswordResetRepository passwordResetRepository
    ) {
        this.passwordResetService = passwordResetService;
        this.passwordResetRepository = passwordResetRepository;
    }

    @EventListener
    public void handlePasswordResetEvent(PasswordResetEvent event) {
        UUID uuid = UUID.randomUUID();
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setEmail(event.getUser().getEmail());
        passwordReset.setAttempts(1);
        passwordReset.setToken(uuid);
        passwordReset.setTokenExpiresAt(LocalDateTime.now().plusHours(1));

        this.passwordResetService.createPasswordReset(passwordReset);
    }

}
