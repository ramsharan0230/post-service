package com.video.processing.entities;

import java.time.LocalDateTime;

import com.video.processing.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "auth_tokens")
@ToString
@Builder
public class AuthToken {

    @Id
    private String token;

    private Long userId;

    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private TokenType status;

    @Column(name = "is_login_token")
    private boolean isLoginToken;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    public void setDefaultStatus() {
        if (status == null) {
            status = TokenType.PENDING;
        }
    }
}
