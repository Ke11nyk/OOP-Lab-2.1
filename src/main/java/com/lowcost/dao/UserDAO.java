package com.lowcost.dao;

import com.lowcost.db.LowCostDBConnection;
import com.lowcost.model.User;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.time.LocalDateTime;

@Log4j2
public class UserDAO {
    private final Connection connection;

    public UserDAO() {
        this.connection = LowCostDBConnection.getConnection();
    }

    public User save(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (id, email, full_name, registered_at) " +
                            "VALUES (?, ?, ?, ?)");

            statement.setString(1, user.getId());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getFullName());
            statement.setTimestamp(4, Timestamp.valueOf(user.getRegisteredAt()));

            statement.executeUpdate();
            return user;
        } catch (SQLException e) {
            log.error("Error creating user: " + user.toString(), e);
            return null;
        }
    }

    public User findById(String id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM users WHERE id = ?");
            statement.setString(1, id);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return User.builder()
                        .id(rs.getString("id"))
                        .email(rs.getString("email"))
                        .fullName(rs.getString("full_name"))
                        .registeredAt(rs.getTimestamp("registered_at").toLocalDateTime())
                        .build();
            }
        } catch (SQLException e) {
            log.error("Error finding user by id: {}", id, e);
        }
        return null;
    }
}