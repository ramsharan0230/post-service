package com.video.processing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.video.processing.entities.PasswordReset;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long>{}
