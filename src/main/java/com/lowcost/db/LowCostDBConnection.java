package com.lowcost.db;

import lombok.SneakyThrows;

import java.sql.*;

public class LowCostDBConnection {
    private static Connection conn;

    @SneakyThrows({ClassNotFoundException.class, SQLException.class})
    public static Connection getConnection() {
        if(conn == null) {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                    "postgres", "password");
        }
        return conn;
    }
}