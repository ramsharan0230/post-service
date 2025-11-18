package com.video.processing.events;

import com.video.processing.entities.User;

public class PasswordResetEvent {
    private final User user;
    
    public PasswordResetEvent(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }
}
