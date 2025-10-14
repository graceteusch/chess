package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccessObject {

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clearUsers();


    void createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken);

    void clearAuths(String authToken) throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames();

    String checkIfColorTaken(int gameID, String playerColor);

    void updateGame(int gameID, String playerColor, String username);

    void clearGames() throws DataAccessException;

}
