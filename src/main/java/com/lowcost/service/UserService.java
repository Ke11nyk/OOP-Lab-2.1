package com.lowcost.service;

import com.lowcost.dao.UserDAO;
import com.lowcost.model.User;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User getUserById(int id) {
        return userDAO.findById(id);
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            return null;
        }

        // Checking if a user with that email already exists
        if (userDAO.findByEmail(user.getEmail()) != null) {
            return null; // Користувач з таким email вже існує
        }

        // Hashing the password before saving
        user.setPassword(hashPassword(user.getPassword()));
        user.setRegisteredAt(LocalDateTime.now());

        return userDAO.save(user);
    }

    // Method for user authentication
    public User authenticate(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user != null && user.getPassword().equals(hashPassword(password))) {
            return user;
        }
        return null;
    }

    // Password hashing method
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}
