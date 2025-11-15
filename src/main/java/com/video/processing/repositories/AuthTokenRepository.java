package com.video.processing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.video.processing.entities.AuthToken;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
}
