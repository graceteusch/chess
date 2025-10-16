package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccessObject implements DataAccessObject {
    private final HashMap<String, UserData> users = new HashMap<>();
    private final HashMap<String, AuthData> auths = new HashMap<>();
    private final HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clearAuths() {
        auths.clear();
    }

    @Override
    public void createGame(GameData game) {
        games.put(String.valueOf(game.gameID()), game);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(String.valueOf(gameID));
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public String checkIfColorTaken(int gameID, String playerColor) {
        return "";
    }

    @Override
    public void updateGame(int gameID, String playerColor, String username) {

    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
