package ui;

import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;

public class WebSocketFacade extends Endpoint {
    private final String serverUrl;
    Session session;

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
                    System.out.print(SET_TEXT_COLOR_GREEN);
                    System.out.println(message);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ServerResponseException(e.getMessage());
        }
    }

    public void joinGame(int actualID, String color, AuthData currUser) throws ServerResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, currUser.authToken(), actualID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ServerResponseException(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
