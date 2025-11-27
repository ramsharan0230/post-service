package com.video.processing.listeners;

import com.video.processing.events.AuthPasswordResetToken;
import com.video.processing.services.AuthTokenService;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Component
public class AuthPasswordResetTokenListener {
    private final AuthTokenService authTokenService;
    private final Logger logger = Logger.getLogger(AuthPasswordResetTokenListener.class.getName());

    public AuthPasswordResetTokenListener(AuthTokenService authTokenService1){
        this.authTokenService = authTokenService1;
    }

    public void handleUserCreated(AuthPasswordResetToken authPasswordResetTokenEvent) {
        var authTokenToBeCreated = authPasswordResetTokenEvent.getAuthToken();
        logger.info("Auth token to be created: "+authTokenToBeCreated.toString());
        try {
            authTokenService.createAuthToken(authTokenToBeCreated);
        } catch (Exception exception) {
            logger.info("Something went wrong while creating Auth Token: "+exception.getMessage());
        }
    }
}
