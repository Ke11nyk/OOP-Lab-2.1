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
                    "INSERT INTO users (email, full_name, password, registered_at) " +
                            "VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getPassword());
            statement.setTimestamp(4, Timestamp.valueOf(user.getRegisteredAt()));

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // Отримуємо згенерований ID
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            return user;
        } catch (SQLException e) {
            log.error("Error creating user: " + user.toString(), e);
            return null;
        }
    }

    public User findById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM users WHERE id = ?");
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return User.builder()
                        .id(rs.getInt("id"))
                        .email(rs.getString("email"))
                        .fullName(rs.getString("full_name"))
                        .password(rs.getString("password"))
                        .registeredAt(rs.getTimestamp("registered_at").toLocalDateTime())
                        .build();
            }
        } catch (SQLException e) {
            log.error("Error finding user by id: {}", id, e);
        }
        return null;
    }

    public User findByEmail(String email) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM users WHERE email = ?");
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return User.builder()
                        .id(rs.getInt("id"))
                        .email(rs.getString("email"))
                        .fullName(rs.getString("full_name"))
                        .password(rs.getString("password"))
                        .registeredAt(rs.getTimestamp("registered_at").toLocalDateTime())
                        .build();
            }
        } catch (SQLException e) {
            log.error("Error finding user by email: {}", email, e);
        }
        return null;
    }
}