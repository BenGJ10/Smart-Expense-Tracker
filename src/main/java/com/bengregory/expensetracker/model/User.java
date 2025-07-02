package com.bengregory.expensetracker.model;

import com.bengregory.expensetracker.util.InvalidInputException;

public class User {
    private int id;
    private String username;
    private String password;

    public User(int id, String username, String password) throws InvalidInputException {
        setId(id);
        setUsername(username);
        setPassword(password);
    }

    public int getId() { return id; }
    public void setId(int id) throws InvalidInputException {
        if (id < 0) throw new InvalidInputException("User ID cannot be negative");
        this.id = id;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }
        if (username.length() > 50) {
            throw new InvalidInputException("Username cannot exceed 50 characters");
        }
        this.username = username.trim();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) throws InvalidInputException {
        if (password == null || password.length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters");
        }
        this.password = password;
    }
}