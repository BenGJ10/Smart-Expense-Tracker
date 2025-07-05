package com.bengregory.expensetracker.util;

import com.bengregory.expensetracker.model.User;

public class SessionManager {
    private static SessionManager instance;
    private User loggedInUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
    }
}