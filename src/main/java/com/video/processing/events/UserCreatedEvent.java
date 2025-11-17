package com.video.processing.events;

import com.video.processing.entities.User;

public class UserCreatedEvent {
    private final User user;
    
    public UserCreatedEvent(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }
}
