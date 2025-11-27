package com.video.processing.controllers;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.video.processing.dtos.EmailRequest;
import com.video.processing.dtos.LoginRequest;
import com.video.processing.dtos.LoginResponse;
import com.video.processing.entities.User;
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

    @PostMapping("/forget-password")
    public ResponseEntity<ResponseFromApi<User>> forgetPassword(@RequestBody EmailRequest emailRequest){
        logger.info(emailRequest.getEmail());

        User user = this.authService.findUserByEmail(emailRequest.getEmail());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseFromApi.success(user, "User fetched successfully."));
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseFromApi<User>> verifyTokenForForgetPassword(@RequestParam("token") String token){
        User user = this.authService.verifyPasswordResetToken(token);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseFromApi.success(user, "User fetched successfully."));
    }
}
