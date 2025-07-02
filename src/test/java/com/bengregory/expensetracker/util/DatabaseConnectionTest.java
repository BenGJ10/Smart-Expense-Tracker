package com.bengregory.expensetracker.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    @DisplayName("Should establish and close database connection successfully")
    void testConnection() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open after creation");

            // Checking in terminal
            System.out.println("Connection established successfully.");

            // Close connection
            DatabaseConnection.closeConnection(conn);
            assertTrue(conn.isClosed(), "Connection should be closed after calling close");

        } catch (DatabaseException | SQLException e) {
            fail("Exception during DB connection test: " + e.getMessage());
        }
    }
}
