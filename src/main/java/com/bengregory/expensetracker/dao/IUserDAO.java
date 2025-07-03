package com.bengregory.expensetracker.dao;
import com.bengregory.expensetracker.model.User;
import com.bengregory.expensetracker.util.DatabaseException;
import com.bengregory.expensetracker.util.InvalidInputException;

/*
    DAO: Data Access Object
    It’s a design pattern used to abstract and encapsulate all access to a data source, like a database.
 */
public interface IUserDAO {
    void registerUser(String username, String password) throws InvalidInputException, DatabaseException;
    User loginUser(String username, String password) throws InvalidInputException, DatabaseException;
    User getUserById(int id) throws DatabaseException;
}
