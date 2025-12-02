package ui;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;

public class WebSocketFacade extends Endpoint {
    private final String serverUrl;
    Session session;
    private ChessGame.TeamColor color;
    private GameplayClient gameplayClient;

    public WebSocketFacade(String url) {
        try {
            serverUrl = url.replace("http", "ws");
            URI socketURI = new URI(serverUrl + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    gameplayClient.onMessage(message);
//                    System.out.print(SET_TEXT_COLOR_GREEN);
//                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
//                        LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
//                        ChessGame game = loadGame.getGame();
//                        BoardDrawer.drawBoard(game.getBoard(), color);
//                    }
//                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
//                        NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
//                        System.out.println(notification.getMessage());
//                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ServerResponseException(e.getMessage());
        }
    }

    public void addGameplayClient(GameplayClient gameplayClient) {
        this.gameplayClient = gameplayClient;
    }

    public void joinGame(int actualID, String color, AuthData currUser) throws ServerResponseException {
        try {
            ChessGame.TeamColor currTeam;
            if (color.equalsIgnoreCase("WHITE") || color.equals("observer")) {
                this.color = ChessGame.TeamColor.WHITE;
            } else {
                this.color = ChessGame.TeamColor.BLACK;
            }
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, currUser.authToken(), actualID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ServerResponseException(ex.getMessage());
        }
    }

    public void leaveGame(int actualID, AuthData currUser) {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, currUser.authToken(), actualID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ServerResponseException(ex.getMessage());
        }
    }

    public void makeMove(int actualID, AuthData currUser, ChessMove move) {
        try {
            var action = new MakeMoveCommand(currUser.authToken(), actualID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ServerResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
