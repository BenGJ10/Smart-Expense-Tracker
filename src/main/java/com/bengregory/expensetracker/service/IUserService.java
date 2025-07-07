package com.bengregory.expensetracker.service;

import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;

/*
    The service layer acts as an intermediary between controllers (UI logic) and DAOs (database access), handling business logic
    like validations, calculations, or data transformations.
    The service layer isolates business logic from controllers and DAOs.
    Keeps controllers focused on UI interactions and DAOs on database queries, making code more modular and easier to maintain.
 */

public interface IUserService {
    void registerUser(String username, String password) throws InvalidInputException, DatabaseException;
    User loginUser(String username, String password) throws InvalidInputException, DatabaseException;
    User getUserById(int id) throws DatabaseException;
    void updateUser(User user) throws InvalidInputException, DatabaseException;

}