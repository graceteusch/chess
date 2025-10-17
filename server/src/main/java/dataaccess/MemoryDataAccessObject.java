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
    private final HashMap<Integer, GameData> games = new HashMap<>();

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
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public boolean isColorTaken(int gameID, String playerColor) {
        if (playerColor.equals("WHITE") && games.get(gameID).whiteUsername() != null) {
            return true;
        } else if ((playerColor.equals("BLACK") && games.get(gameID).blackUsername() != null)) {
            return true;
        }
        return false;
    }

    @Override
    public void updateGame(int gameID, String playerColor, String username) {
        GameData game = games.get(gameID);
        if (playerColor.equals("WHITE")) {
            GameData updatedGameData = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
        } else {
            GameData updatedGameData = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
        }
    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
