package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import model.GameData;

import java.util.Collection;
import java.util.Random;

public class GameService {
    private final DataAccessObject dataAccess;
    int newGameID = 1;

    private int generateGameID() throws DataAccessException {
        Random random = new Random();
        int randomID = random.nextInt();
        while (dataAccess.getGame(randomID) != null) {
            randomID = random.nextInt();
        }
        if (randomID <= 0) {
            while (randomID <= 0) {
                randomID = random.nextInt();
            }
        }
        return randomID;
    }

    private boolean isColorTaken(int gameID, String playerColor) throws DataAccessException {
        var game = dataAccess.getGame(gameID);
        if (game != null) {
            if (playerColor.equalsIgnoreCase("WHITE") && game.whiteUsername() != null) {
                return true;
            } else if (playerColor.equalsIgnoreCase("BLACK") && game.blackUsername() != null) {
                return true;
            }
        }
        return false;
    }


    public GameService(DataAccessObject dataAccess) {
        this.dataAccess = dataAccess;

    }

    public int createGame(String authToken, String name) throws BadRequestException, UnauthorizedException, DataAccessException {

        // 400: bad request
        if (name == null) {
            throw new BadRequestException("Invalid name. Make sure you provide a name for the game.");
        }
        // 401: unauthorized
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("You are not authorized to create a game.");
        }

        int newGameID = generateGameID();
        var newGame = new GameData(newGameID, null, null, name, new ChessGame());
        dataAccess.createGame(newGame);
        return newGameID;
    }

    public void joinGame(String authToken, String playerColor, Integer gameID)
            throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException {
        // 400: bad request - playerColor is NOT black/white, gameID is null, or gameID doesn't exist in the db
        if (playerColor == null) {
            throw new BadRequestException("Invalid color. Make sure you specify either white or black.");
        }
        if (!playerColor.equalsIgnoreCase("BLACK") && !playerColor.equalsIgnoreCase("WHITE")) {
            throw new BadRequestException("Invalid color. Make sure you specify either white or black.");
        }
        // 400: bad request - gameID is null or game doesn't exist in the db
        if (gameID == null || dataAccess.getGame(gameID) == null) {
            throw new BadRequestException("The game you requested to join does not exist.");
        }
        // 401: unauthorized
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("You are not authorized to join a game.");
        }
        try {
            if (isColorTaken(gameID, playerColor)) {
                throw new AlreadyTakenException("This color is already taken by another player.");
            }
        } catch (DataAccessException ex) {
            throw new BadRequestException("bad request");
        }

        var authData = dataAccess.getAuth(authToken);
        var username = authData.username();
        dataAccess.updateGame(gameID, playerColor, username, null);
    }

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("You are not authorized to list games.");
        }
        Collection<GameData> allGames = dataAccess.listGames();
        return allGames;
    }
}
