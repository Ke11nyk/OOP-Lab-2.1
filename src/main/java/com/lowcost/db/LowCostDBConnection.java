package com.lowcost.db;

import lombok.SneakyThrows;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class LowCostDBConnection {
    private static Connection conn;
    private static final Dotenv dotenv = Dotenv.configure().load();

    @SneakyThrows({ClassNotFoundException.class, SQLException.class})
    public static Connection getConnection() {
        if(conn == null) {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dotenv.get("DB_URL"),
                    dotenv.get("DB_USERNAME"), dotenv.get("DB_PASSWORD"));
        }
        return conn;
    }
}