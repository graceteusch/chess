package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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

        if (params.length == 2) {
            String currPiecePosition = params[0];
            String newPosition = params[1];

            ChessPosition currPos = getChessPosition(currPiecePosition);
            ChessPosition newPos = getChessPosition(newPosition);
            //TODO: include implementation for promotion (??)
            ChessMove move = new ChessMove(currPos, newPos, null);


            return String.format("You made the move" + params[0] + " " + params[1] + ".");
        } else {
            System.out.println("Invalid input");
            throw new ServerResponseException("To make a move, use the following format: a2 a4 (for example)");
        }
    }

    private ChessPosition getChessPosition(String input) {
        if (input.length() != 2) {
            System.out.println("Invalid input");
            throw new ServerResponseException("To make a move, use the following format: a2 a4 (for example)");
        }
        char colChar = input.charAt(0);
        char rowChar = input.charAt(0);

        // check if col char can be converted to a valid number
        int col = colToInt(colChar);

        // check if row char is a valid number
        int row;
        try {
            row = Integer.parseInt(String.valueOf(rowChar));
        } catch (NumberFormatException ex) {
            System.out.println("Invalid input");
            throw new ServerResponseException("To make a move, use the following format: a2 a4 (for example)");
        }
        if (row < 1 || row > 8) {
            System.out.println("Invalid input");
            throw new ServerResponseException("Make sure you specify a valid position on the board (columns a-h, rows 1-8)");
        }

        return new ChessPosition(row, col);
    }

    private int colToInt(char colChar) {
        return switch (Character.toLowerCase(colChar)) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default ->
                    throw new ServerResponseException("Make sure you specify a valid position on the board (columns a-h, rows 1-8)");
        };
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
