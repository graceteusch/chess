package dataaccess;

import model.AuthData;
import model.GameData;

public interface AuthDAO {
    // CREATE
    void createAuth(AuthData auth) throws DataAccessException;

    // READ
    AuthData getAuth(String authToken) throws DataAccessException;

    // UPDATE
    // ????
    // DELETE
    void removeAuth(String authToken) throws DataAccessException;
}
