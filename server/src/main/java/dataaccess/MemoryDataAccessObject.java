package dataaccess;

import chess.ChessGame;
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
    public Collection<AuthData> listAuths() throws DataAccessException {
        return List.of();
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
    public void updateGame(int gameID, String playerColor, String username, ChessGame updatedGame) {
        GameData game = games.get(gameID);
        if (playerColor.equals("WHITE")) {
            GameData updatedGameData = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
            games.put(gameID, updatedGameData);
        } else {
            GameData updatedGameData = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
            games.put(gameID, updatedGameData);
        }
    }

    @Override
    public void clearGames() {
        games.clear();
    }
}
