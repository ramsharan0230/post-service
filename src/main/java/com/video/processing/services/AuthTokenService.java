package com.video.processing.services;

import com.video.processing.entities.AuthToken;
import com.video.processing.repositories.AuthTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {
    private final AuthTokenRepository authTokenRepository;

    public AuthTokenService(AuthTokenRepository authTokenRepository){
        this.authTokenRepository = authTokenRepository;
    }

    public AuthToken createAuthToken(AuthToken authToken){
        return this.authTokenRepository.save(authToken);
    }
}
