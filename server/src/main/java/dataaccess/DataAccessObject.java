package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccessObject {

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clearUsers() throws DataAccessException;


    void createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken);

    void clearAuths() throws DataAccessException;

    void createGame(GameData game);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    boolean isColorTaken(int gameID, String playerColor);

    void updateGame(int gameID, String playerColor, String username);

    void clearGames();

}
