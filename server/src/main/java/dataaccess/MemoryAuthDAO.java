package dataaccess;

import model.AuthData;

import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void removeAuth(String authToken) throws DataAccessException {

    }
}
