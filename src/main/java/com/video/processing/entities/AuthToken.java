package com.video.processing.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "auth_tokens")
public class AuthToken {

    @Id
    private String token;

    private Long userId;

    private LocalDateTime expiresAt;

    public AuthToken() {}

    public AuthToken(String token, Long userId, LocalDateTime expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

}
