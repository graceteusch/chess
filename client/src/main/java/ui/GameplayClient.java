package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

public class GameplayClient implements Client {

    private final ServerFacade server;
    private final Repl repl;
    private final AuthData currUser;
    private final WebSocketFacade ws;

    private int gameID;
    private ChessGame currGame;
    private ChessGame.TeamColor color;


    public GameplayClient(ServerFacade server, Repl repl, AuthData auth, WebSocketFacade ws, int gameID, String color) {
        this.server = server;
        this.repl = repl;
        this.currUser = auth;
        this.ws = ws;
        this.gameID = gameID;
        if (color.equalsIgnoreCase("WHITE")) {
            this.color = ChessGame.TeamColor.WHITE;
        } else if (color.equalsIgnoreCase("BLACK")) {
            this.color = ChessGame.TeamColor.BLACK;
        } else if (color.equalsIgnoreCase("observer")) {
            this.color = null;
        }
    }

    public void updateGame(ChessGame game) {
        this.currGame = game;
    }

    @Override
    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawBoard(params);
                case "leave" -> leave(params);
                case "move" -> makeMove(params);
                case "resign" -> resign(params);
                case "highlight" -> highlightMoves(params);
                case "quit" -> "Quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public void onMessage(String message) {
        System.out.print(SET_TEXT_COLOR_GREEN);
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
            this.currGame = loadGame.getGame();
            BoardDrawer.drawBoard(currGame.getBoard(), color);
        }
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
            System.out.println(notification.getMessage());
        }
    }

    private String redrawBoard(String[] params) {
        if (params.length == 0) {
            if (color == null) {
                BoardDrawer.drawBoard(currGame.getBoard(), ChessGame.TeamColor.WHITE);
            } else {
                BoardDrawer.drawBoard(currGame.getBoard(), color);
            }
            return "";
        } else {
            System.out.println("Invalid input");
            throw new ServerResponseException("To redraw the board, please use the following format: Redraw");
        }
    }

    private String leave(String[] params) {
        if (params.length == 0) {
            ws.leaveGame(gameID, currUser);
            repl.setClient(new PostloginClient(server, repl, currUser));
            repl.setState(ReplState.LOGGEDIN);
            return "You left the game.";
        } else {
            System.out.println("Invalid input");
            throw new ServerResponseException("To leave a game, please use the following format: leave");
        }
    }

    private String makeMove(String[] params) {
        if (color == null) {
            return "You cannot make moves as an observer.";
        }
        return null;
    }

    private String resign(String[] params) {
        if (color == null) {
            return "You cannot resign as an observer.";
        }
        return null;
    }


    private String highlightMoves(String[] params) {
        return "";
    }


    @Override
    public String help() {
        return """
                Commands you can use:
                - Redraw - redraw the game board
                - Leave - leave the game
                - Move <PIECE POSITION> <POSITION TO MOVE TO> - make a move. Use the format 'a2 a4' to represent the move you want to make.
                - Resign - Forfeit the game
                - Highlight <PIECE POSITION> - highlight all legal moves for a given piece. Use the format 'a2' to represent the piece.
                - Help - list all possible commands!
                """;
    }
}
