package com.bengregory.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static final String PROPERTIES_FILE = "db.properties";

    public static Connection getConnection() throws DatabaseException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
            props.load(fis);
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);

        } catch (IOException | SQLException e) {
            throw new DatabaseException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
    // Utility to close connection safely
    public static void closeConnection(Connection conn) throws DatabaseException {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DatabaseException("Failed to close database connection", e);
            }
        }
    }
}