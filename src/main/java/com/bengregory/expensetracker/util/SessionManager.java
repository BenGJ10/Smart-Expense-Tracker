package com.bengregory.expensetracker.util;

import com.bengregory.expensetracker.model.User;

// We use a singleton class to store the user object in memory during the session.
// It’s designed to be a singleton — meaning only one instance exists at a time for the whole application.

public class SessionManager {
    private static SessionManager instance; // This holds the one and only instance of SessionManager
    private User loggedInUser; // Stores current user who logged in

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) { // If it hasn’t been created yet (null), it creates one
            instance = new SessionManager();
        }
        return instance; // Otherwise, it returns the already-created instance
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() { // Returns the currently logged-in user
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
    } // Clears the session
}