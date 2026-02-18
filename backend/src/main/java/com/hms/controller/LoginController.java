package com.hms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hms.entity.User;
import com.hms.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(
    origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500"
    }
)
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public User login(@RequestBody User user) {

        System.out.println("LOGIN API HIT");
        System.out.println("EMAIL RECEIVED: " + user.getEmail());
        System.out.println("PASSWORD RECEIVED: " + user.getPassword());

        User dbUser = userService.login(
                user.getEmail(),
                user.getPassword()
        );

        System.out.println("USER FROM DB: " + dbUser);

        return dbUser;   // returns null if invalid
    }
}
