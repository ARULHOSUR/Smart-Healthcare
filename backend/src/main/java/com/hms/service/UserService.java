package com.hms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hms.entity.User;
import com.hms.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User login(String email, String password) {

        User user = repo.findByEmail(email);

        if (user == null) {
            System.out.println("NO USER FOUND FOR EMAIL: " + email);
            return null;
        }

        System.out.println("DB PASSWORD: " + user.getPassword());

        if (user.getPassword().equals(password)) {
            System.out.println("PASSWORD MATCHED");
            return user;
        }

        System.out.println("PASSWORD MISMATCH");
        return null;
    }
}
