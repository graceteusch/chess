package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    // CREATE
    void createGame(GameData game) throws DataAccessException;

    // READ (get information)
    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames();

    String checkIfColorTaken(int gameID, String playerColor);

    // UPDATE
    void updateGame(int gameID, String playerColor, String username);

    // DELETE
    void removeGame(int gameID) throws DataAccessException;
}
