package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static chess.ChessGame.TeamColor.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private DataAccessObject dataAccess;
    private final ConnectionManager connections = new ConnectionManager();
    private boolean gameOver;

    public WebSocketHandler(DataAccessObject dataAccess) {
        this.dataAccess = dataAccess;
        gameOver = false;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) throws DataAccessException {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            try {
                checkValidCommand(action);
            } catch (DataAccessException ex) {
                ServerMessage error = new ErrorMessage(ex.getMessage());
                sendMessage(error, ctx.session);
                return;
            }

            switch (action.getCommandType()) {
                case CONNECT -> connect(action.getAuthToken(), action.getGameID(), ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveAction = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveAction.getAuthToken(), moveAction.getGameID(), moveAction.getMove(), ctx.session);
                }
                case LEAVE -> leave(action.getAuthToken(), action.getGameID(), ctx.session);
                case RESIGN -> resign(action.getAuthToken(), action.getGameID(), ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void checkValidCommand(UserGameCommand command) throws DataAccessException {
        var authToken = command.getAuthToken();
        var gameID = command.getGameID();
        AuthData auth = dataAccess.getAuth(authToken);
        GameData gameData = dataAccess.getGame(gameID);
        if (auth == null) {
            throw new DataAccessException("Error: invalid registration/login");
        }
        if (gameData == null) {
            throw new DataAccessException("Error: invalid game");
        }
    }

    private void connect(String authToken, Integer gameID, Session session) throws IOException, DataAccessException {
        // server received a CONNECT message from the client

        // 1. Server sends a LOAD_GAME message back to the root client.
        ChessGame game = dataAccess.getGame(gameID).game();
        ServerMessage loadGame = new LoadGameMessage(game);
        sendMessage(loadGame, session);

        // 2. Server sends a NOTIFICATION message to all other clients in that
        //    game informing them the root client connected to the game, either as a player
        //    (in which case their color must be specified) or as an observer.
        String user = dataAccess.getAuth(authToken).username();
        String whiteUser = dataAccess.getGame(gameID).whiteUsername();
        String blackUser = dataAccess.getGame(gameID).blackUsername();
        String color;
        if (user.equals(whiteUser)) {
            color = "white";
        } else if (user.equals(blackUser)) {
            color = "black";
        } else {
            color = "observer";
        }

        connections.add(session, gameID);
        String msg;
        if (color.equals("observer")) {
            msg = String.format("%s joined the game as an observer.", user);
        } else {
            msg = String.format("%s joined the game as the %s player.", user, color);
        }
        ServerMessage notify = new NotificationMessage(msg);
        connections.broadcast(session, notify, gameID);
    }

    private void sendMessage(ServerMessage message, Session session) throws IOException {
        String strMessage = new Gson().toJson(message);
        session.getRemote().sendString(strMessage);
    }

    private void makeMove(String authToken, Integer gameID, ChessMove move, Session session) throws DataAccessException, IOException {
        String user = dataAccess.getAuth(authToken).username();
        ChessGame game = dataAccess.getGame(gameID).game();
        String whiteUser = dataAccess.getGame(gameID).whiteUsername();
        String blackUser = dataAccess.getGame(gameID).blackUsername();

        // check if game is already over
        if (game.getGameStatus()) {
            ServerMessage error = new ErrorMessage("Error: this game is over");
            sendMessage(error, session);
            return;
        }

        // check whose turn it is
        ChessGame.TeamColor currTurn = game.getTeamTurn();
        if (currTurn.equals(WHITE) && !user.equals(whiteUser)) {
            ServerMessage error = new ErrorMessage("Error: wrong team's turn");
            sendMessage(error, session);
            return;
        } else if (currTurn.equals(BLACK) && !user.equals(blackUser)) {
            ServerMessage error = new ErrorMessage("Error: wrong team's turn");
            sendMessage(error, session);
            return;
        }

        ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());


        if (piece == null) {
            ServerMessage error = new ErrorMessage("Error: invalid move (no piece at start position)");
            sendMessage(error, session);
            return;
        }

        // check if the piece being moved is the correct color
        if (currTurn.equals(WHITE) && piece.getTeamColor() != WHITE) {
            ServerMessage error = new ErrorMessage("Error: trying to move opponent's piece");
            sendMessage(error, session);
            return;
        } else if (currTurn.equals(BLACK) && piece.getTeamColor() != BLACK) {
            ServerMessage error = new ErrorMessage("Error: trying to move opponent's piece");
            sendMessage(error, session);
            return;
        }


        if (move.getPromotionPiece() != null && piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            sendMessage(new ErrorMessage("Error: only pawns can be promoted"), session);
            return;
        }

        if (move.getPromotionPiece() == null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (piece.getTeamColor() == WHITE && move.getStartPosition().getRow() == 7 && move.getEndPosition().getRow() == 8) {
                sendMessage(new ErrorMessage("Error: when moving a pawn to the end of the board, you must provide a promotion piece type"), session);
                return;
            } else if (piece.getTeamColor() == BLACK && move.getStartPosition().getRow() == 2 && move.getEndPosition().getRow() == 1) {
                sendMessage(new ErrorMessage("Error: when moving a pawn to the end of the board, you must provide a promotion piece type"), session);
                return;
            }
        }

        // make move (and catch InvalidException)
        try {
            game.makeMove(move);
        } catch (InvalidMoveException ex) {
            ServerMessage error = new ErrorMessage("Error: invalid move");
            sendMessage(error, session);
            return;
        }

        // update game in the db
        dataAccess.updateGame(gameID, null, null, game);

        // Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
        ChessGame updatedGame = dataAccess.getGame(gameID).game();
        ServerMessage loadGame = new LoadGameMessage(updatedGame);
        sendMessage(loadGame, session);
        connections.broadcast(session, loadGame, gameID);

        int startCol = move.getStartPosition().getColumn();
        String startColStr = "";
        if (startCol == 1) {
            startColStr = "a";
        } else if (startCol == 2) {
            startColStr = "b";
        } else if (startCol == 3) {
            startColStr = "c";
        } else if (startCol == 4) {
            startColStr = "d";
        } else if (startCol == 5) {
            startColStr = "e";
        } else if (startCol == 6) {
            startColStr = "f";
        } else if (startCol == 7) {
            startColStr = "g";
        } else if (startCol == 8) {
            startColStr = "h";
        }
        String startRow = String.valueOf(move.getStartPosition().getRow());

        int endCol = move.getEndPosition().getColumn();
        String endColStr = "";
        if (endCol == 1) {
            endColStr = "a";
        } else if (endCol == 2) {
            endColStr = "b";
        } else if (endCol == 3) {
            endColStr = "c";
        } else if (endCol == 4) {
            endColStr = "d";
        } else if (endCol == 5) {
            endColStr = "e";
        } else if (endCol == 6) {
            endColStr = "f";
        } else if (endCol == 7) {
            endColStr = "g";
        } else if (endCol == 8) {
            endColStr = "h";
        }
        String endRow = String.valueOf(move.getEndPosition().getRow());

        // Server sends a Notification message to all other clients in that game informing them what move was made.
        connections.broadcast(session, new NotificationMessage(String.format("%s made the move %s%s -> %s%s.", user, startColStr, startRow, endColStr, endRow)), gameID);

        // If the move results in check, checkmate or stalemate the server sends a Notification message to all clients.
        if (game.isInCheckmate(game.getTeamTurn())) {
            connections.broadcast(null, new NotificationMessage(String.format("%s is in checkmate.", game.getTeamTurn())), gameID);
        } else if (game.isInStalemate(game.getTeamTurn())) {
            connections.broadcast(null, new NotificationMessage(String.format("%s is in stalemate.", game.getTeamTurn())), gameID);
        } else if (game.isInCheck(game.getTeamTurn())) {
            connections.broadcast(null, new NotificationMessage(String.format("%s is in check.", game.getTeamTurn())), gameID);
        }

    }


    private void leave(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        // server received a LEAVE message from the client

        // game is updated to remove the user
        String user = dataAccess.getAuth(authToken).username();
        String whiteUser = dataAccess.getGame(gameID).whiteUsername();
        String blackUser = dataAccess.getGame(gameID).blackUsername();
        if (user.equals(whiteUser)) {
            // set white username to null
            dataAccess.updateGame(gameID, "WHITE", null, null);
        }

        if (user.equals(blackUser)) {
            // set black username to null
            dataAccess.updateGame(gameID, "BLACK", null, null);
        }


        // remove session via connectionManager
        connections.remove(session, gameID);

        // send notification message via connectionManager
        String msg = String.format("%s left the game.", user);
        ServerMessage notify = new NotificationMessage(msg);
        connections.broadcast(session, notify, gameID);
    }

    private void resign(String authToken, Integer gameID, Session session) throws DataAccessException, IOException {
        //Forfeits the match and ends the game (no more moves can be made).
        String user = dataAccess.getAuth(authToken).username();
        String whiteUser = dataAccess.getGame(gameID).whiteUsername();
        String blackUser = dataAccess.getGame(gameID).blackUsername();
        ChessGame game = dataAccess.getGame(gameID).game();
        if (!user.equals(whiteUser) && !user.equals(blackUser)) {
            ServerMessage error = new ErrorMessage("Error: you cannot resign as an observer");
            sendMessage(error, session);
            return;
        }

        if (game.getGameStatus()) {
            ServerMessage error = new ErrorMessage("Error: game is over");
            sendMessage(error, session);
            return;
        }


        game.setGameStatus(true);
        dataAccess.updateGame(gameID, null, null, game);

        // send notification message via connectionManager
        String msg = String.format("%s resigned from the game.", user);
        ServerMessage notify = new NotificationMessage(msg);
        connections.broadcast(null, notify, gameID);
    }


}
