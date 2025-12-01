package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import dataaccess.SqlDataAccess;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private DataAccessObject dataAccess;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(SqlDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    //private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.GetCommandType()) {
                case CONNECT -> connect(action.getAuthToken(), action.getGameID(), ctx.session);
                case MAKE_MOVE -> makeMove(action.getAuthToken(), action.getGameID(), ctx.session);
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


    // TODO: implement methods for the different message cases

    private void connect(String authToken, Integer gameID, Session session) throws IOException, DataAccessException {
        // server received a CONNECT message from the client

        // 1. Server sends a LOAD_GAME message back to the root client.
        ChessGame game = dataAccess.getGame(gameID).game();
        String user = dataAccess.getAuth(authToken).username();

        ServerMessage loadGame = new LoadGameMessage(game);
        sendMessage(loadGame, session);

        // 2. Server sends a NOTIFICATION message to all other clients in that
        //    game informing them the root client connected to the game, either as a player
        //    (in which case their color must be specified) or as an observer.
        connections.add(session, gameID);
        String msg = String.format("%s joined the game as the %s player", user, "color");
        ServerMessage notify = new NotificationMessage(msg);
        connections.broadcast(session, notify, gameID);
    }

    private void sendMessage(ServerMessage message, Session session) throws IOException {
        String strMessage = new Gson().toJson(message);
        session.getRemote().sendString(strMessage);
    }

    private void makeMove(String authToken, Integer gameID, Session session) {
        // Used to request to make a move in a game.
    }


    private void leave(String authToken, Integer gameID, Session session) {
        //Tells the server you are leaving the game so it will stop sending you notifications.
    }

    private void resign(String authToken, Integer gameID, Session session) {
        //Forfeits the match and ends the game (no more moves can be made).
    }


//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(session, notification);
//    }
//
//    private void exit(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }
}
