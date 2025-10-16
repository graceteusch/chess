package services;

import chess.ChessGame;
import dataaccess.DataAccessObject;
import model.GameData;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    private final DataAccessObject dataAccess;

    private int generateGameID() {
        return 1;
    }


    public GameService(DataAccessObject DAO) {
        this.dataAccess = DAO;

    }

    public int createGame(String name) {
        int newGameID = generateGameID();
        var newGame = new GameData(newGameID, null, null, name, new ChessGame());
        dataAccess.createGame(newGame);
        return newGameID;
    }

//    public Collection<GameData> listGames(String authToken) throws UnauthorizedException {
//        if (authToken == null || dataAccess.getAuth(authToken) == null) {
//            throw new UnauthorizedException("unauthorized");
//        }
//        Collection<GameData> allGames = dataAccess.listGames();
//        return allGames;
//    }
}
