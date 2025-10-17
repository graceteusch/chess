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

    public int createGame(String authToken, String name) throws BadRequestException, UnauthorizedException {

        // 400: bad request
        if (name == null) {
            throw new BadRequestException("bad request");
        }
        // 401: unauthorized
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }

        int newGameID = generateGameID();
        var newGame = new GameData(newGameID, null, null, name, new ChessGame());
        dataAccess.createGame(newGame);
        return newGameID;
    }

    public void joinGame(String authToken, String playerColor, Integer gameID) throws BadRequestException, UnauthorizedException {
        // 400: bad request - playerColor is NOT black/white, gameID is null, or gameID doesn't exist in the db
        if (!playerColor.equals("BLACK") && !playerColor.equals("WHITE")) {
            throw new BadRequestException("bad request");
        }
        // 400: bad request - gameID is null or game doesn't exist in the db
        if (gameID == null || dataAccess.getGame(gameID) == null) {
            throw new BadRequestException("bad request");
        }
        // 401: unauthorized
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }
        var authData = dataAccess.getAuth(authToken);
        var username = authData.username();
        dataAccess.updateGame(gameID, playerColor, username);
    }

//    public Collection<GameData> listGames(String authToken) throws UnauthorizedException {
//        if (authToken == null || dataAccess.getAuth(authToken) == null) {
//            throw new UnauthorizedException("unauthorized");
//        }
//        Collection<GameData> allGames = dataAccess.listGames();
//        return allGames;
//    }
}
