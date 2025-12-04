package server;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Session>> connections = new ConcurrentHashMap<>();
    //public final ConcurrentHashMap<Integer, Map<Session, ChessGame.TeamColor>> connectionsColors = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new ArrayList<>());
            connections.get(gameID).add(session);
        } else {
            connections.get(gameID).add(session);
        }
    }

    public void remove(Session session, int gameID) {
        if (connections.containsKey(gameID)) {
            connections.get(gameID).remove(session);
        }
    }

    public void broadcast(Session excludeSession, ServerMessage notification, int gameID) throws IOException {
        String msg = new Gson().toJson(notification);
        if (connections.containsKey(gameID)) {
            ArrayList<Session> sessions = connections.get(gameID);
            for (Session session : sessions) {
                if (session.isOpen()) {
                    if (!session.equals(excludeSession)) {
                        System.out.println("Broadcasting message");
                        session.getRemote().sendString(msg);
                    }
                }
            }
        }
    }
}
