package com.video.processing.services;


import com.video.processing.entities.User;
import com.video.processing.enums.UserStatus;
import com.video.processing.repositories.UserRepository;
import com.video.processing.utilities.PasswordUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Page<User> fetchAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    public User createNewUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        String baseUsername = extractUsernameFromEmail(user.getEmail());
        String username = generateUniqueUsername(baseUsername);
        
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.APPROVED);
        }
        
        return userRepository.save(user);
    }

    private String extractUsernameFromEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return email.split("@")[0].toLowerCase();
    }

    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;
        
        // Clean username - remove any special characters except underscores and dots
        username = username.replaceAll("[^a-zA-Z0-9._]", "");
        
        while (usernameExists(username)) {
            username = baseUsername + counter;
            counter++;
            
            if (counter > 1000) {
                throw new RuntimeException("Unable to generate unique username after 1000 attempts");
            }
        }
        
        return username;
    }

    private boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
