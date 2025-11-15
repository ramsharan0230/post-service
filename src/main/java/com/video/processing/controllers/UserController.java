package com.video.processing.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.video.processing.entities.User;
import com.video.processing.services.UserService;
import com.video.processing.utilities.ResponseFromApi;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService usrService){
        this.userService = usrService;
    }

    @GetMapping
    public ResponseEntity<ResponseFromApi<Page<User>>> fetchAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> users = this.userService.fetchAllUsers(page, size);

        ResponseFromApi<Page<User>> response = ResponseFromApi
                .success(users, "Users are fetched successfully.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping
    public ResponseEntity<ResponseFromApi<User>> createNewUser(@Valid @RequestBody User user) {
        User createdUser = this.userService.createNewUser(user);

        ResponseFromApi<User> response = ResponseFromApi
                .success(createdUser, "User created successfully.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
