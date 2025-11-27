package com.video.processing.events;

import com.video.processing.entities.AuthToken;

public class AuthPasswordResetToken {
    private final AuthToken authToken;

    public AuthPasswordResetToken(AuthToken authToken){
        this.authToken = authToken;
    }

    public AuthToken getAuthToken(){
        return this.authToken;
    }
}
