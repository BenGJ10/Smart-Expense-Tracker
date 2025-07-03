package com.bengregory.expensetracker.util;

public class ValidationUtil {
    public static void validateUsername(String username) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }
        if (username.length() > 50) {
            throw new InvalidInputException("Username cannot exceed 50 characters");
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            throw new InvalidInputException("Username must be alphanumeric");
        }
    }

    public static void validatePassword(String password) throws InvalidInputException {
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty");
        }
        if (password.length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidInputException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new InvalidInputException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new InvalidInputException("Password must contain at least one digit");
        }
        if (password.length() > 30) {
            throw new InvalidInputException("Password cannot exceed 30 characters");
        }
    }

    public static void validateAmount(double amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }
        if (amount > 99999999.99) { // Matches DECIMAL(10,2)
            throw new InvalidInputException("Amount cannot exceed 99999999.99");
        }
    }

    public static void validateCategory(String category) throws InvalidInputException {
        if (category != null && category.length() > 50) {
            throw new InvalidInputException("Category cannot exceed 50 characters");
        }
    }

    public static void validateDescription(String description) throws InvalidInputException {
        if (description != null && description.length() > 1000) {
            throw new InvalidInputException("Description cannot exceed 1000 characters");
        }
    }
}