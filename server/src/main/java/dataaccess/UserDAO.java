package dataaccess;

import model.UserData;

public interface UserDAO {
    // For the most part, the methods on your DAO classes will be CRUD operations that:
    // Create objects in the data store
    // Read objects from the data store
    // Update objects already in the data store
    // Delete objects from the data store

    // create UserData object in the data store
    void insertUser(UserData u) throws DataAccessException;

    // get UserData object in the data store
    UserData getUser(String username) throws DataAccessException;

    // update UserData ?????
    // void updateUser(String username);

    // delete UserData
    void removeUser(String username) throws DataAccessException;
}
