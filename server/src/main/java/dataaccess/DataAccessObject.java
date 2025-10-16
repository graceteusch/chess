package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccessObject {

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String username);

    void clearUsers();


    void createAuth(AuthData auth);

    AuthData getAuth(String authToken);

    void deleteAuth(String authToken);

    void clearAuths();

    void createGame(GameData game);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    String checkIfColorTaken(int gameID, String playerColor);

    void updateGame(int gameID, String playerColor, String username);

    void clearGames();

}
