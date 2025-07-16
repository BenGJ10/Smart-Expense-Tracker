package com.bengregory.expensetracker.util;

import java.sql.Connection; // JDBC classes for DB connections
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties; // Java utility class that holds key-value pairs of configuration
import java.io.FileInputStream;
import java.io.IOException;

/*
    Reads database credentials from a file.
    Creates a connection using JDBC.
    Provides a utility to safely close the connection.
 */

public class DatabaseConnection {
    private static final String PROPERTIES_FILE = "db.properties"; // Constant holding the name of the file that contains DB configuration

    public static Connection getConnection() throws DatabaseException {
        Properties props = new Properties(); // Creates a Properties object to load key-value pairs from the file

        try (FileInputStream fis = new FileInputStream(PROPERTIES_FILE)) {
            props.load(fis); // loads all the values into the props object
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            // Uses JDBC's DriverManager to establish a connection to the database, returns a Connection object that can be used in DAO classes.
            return DriverManager.getConnection(url, user, password);

        } catch (IOException | SQLException e) {
            throw new DatabaseException("Failed to connect to database: " + e.getMessage(), e);
        }
    }
    // Utility to close connection safely
    public static void closeConnection(Connection conn) throws DatabaseException {
        if (conn != null) { // Safely closes a connection if it's not null
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DatabaseException("Failed to close database connection", e);
            }
        }
    }
}