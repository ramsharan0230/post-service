package com.video.processing.controllers;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.video.processing.dtos.LoginRequest;
import com.video.processing.dtos.LoginResponse;
import com.video.processing.services.AuthService;
import com.video.processing.utilities.ResponseFromApi;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final AuthService authService;

    public AuthController(AuthService aService){
        this.authService = aService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseFromApi<LoginResponse>> makeLogin(@RequestBody LoginRequest request){
        logger.info(request.getEmail());
        logger.info(request.getPassword());
        LoginResponse loginResponse = this.authService.login(request);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseFromApi.success(loginResponse, "Logged in successfully."));
    }
}
