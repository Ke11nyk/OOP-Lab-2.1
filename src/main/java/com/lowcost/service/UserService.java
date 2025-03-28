package com.lowcost.service;

import com.lowcost.dao.UserDAO;
import com.lowcost.model.User;
import java.time.LocalDateTime;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User getUserById(String id) {
        return userDAO.findById(id);
    }

    public User createUser(User user) {
        if (user.getId() == null || user.getEmail() == null) {
            return null;
        }
        user.setRegisteredAt(LocalDateTime.now());
        return userDAO.save(user);
    }
}
