package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    @Override
    public void createGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public String checkIfColorTaken(int gameID, String playerColor) {
        return "";
    }

    @Override
    public void updateGame(int gameID, String playerColor, String username) {

    }

    @Override
    public void removeGame(int gameID) throws DataAccessException {

    }
}
