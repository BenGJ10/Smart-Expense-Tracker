package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.dao.IUserDAO;
import com.bengregory.expensetracker.dao.UserDAO;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.CustomLogger;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;
import com.bengregory.expensetracker.util.SessionManager;

public class UserService implements IUserService {
    private final IUserDAO userDAO = new UserDAO();
    private final CustomLogger logger = CustomLogger.getInstance();

    @Override
    public void registerUser(String username, String password) throws InvalidInputException, DatabaseException {
        logger.info("Attempting to register user: " + username);
        userDAO.registerUser(username, password);
        logger.info("User registration successful: " + username);
    }

    @Override
    public User loginUser(String username, String password) throws InvalidInputException, DatabaseException {
        logger.info("Attempting login for user: " + username);
        User user = userDAO.loginUser(username, password);
        SessionManager.getInstance().setLoggedInUser(user);
        logger.info("User login successful: " + username);
        return user;
    }

    @Override
    public User getUserById(int id) throws DatabaseException {
        logger.info("Fetching user by ID: " + id);
        User user = userDAO.getUserById(id);
        logger.info("User retrieved by ID: " + id);
        return user;
    }
}