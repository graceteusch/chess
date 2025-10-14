package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO {
    @Override
    public void insertUser(UserData u) throws DataAccessException {
        
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void removeUser(String username) throws DataAccessException {

    }
}
