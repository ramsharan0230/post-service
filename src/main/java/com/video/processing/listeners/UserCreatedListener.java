package com.video.processing.listeners;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.video.processing.events.UserCreatedEvent;
import com.video.processing.services.EmailService;

@Component
public class UserCreatedListener {
    private final EmailService emailService;
    private final Logger logger = Logger.getLogger(UserCreatedListener.class.getName());

    public UserCreatedListener(EmailService eService) {
        this.emailService = eService;
    }

    public void handleUserCreated(UserCreatedEvent userCreatedEvent) {
        var user = userCreatedEvent.getUser();
        Map<String, Object> variables = Map.of(
                "fullname", user.getFirstName() + " " + user.getLastName(),
                "email", user.getEmail(),
                "firstName", user.getFirstName());

        try {
            emailService.sendMail(
                    user.getEmail(),
                    "Welcome to Our Service!",
                    "welcome",
                    variables
            );
            // Random random = new Random();
		    // System.out.println("random int: "+random.nextInt(100000));

        } catch (Exception exception) {

        }
    }
}
