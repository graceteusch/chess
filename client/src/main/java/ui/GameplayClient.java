package ui;

import model.AuthData;

import java.util.Arrays;

public class GameplayClient implements Client {

    private final ServerFacade server;
    private final Repl repl;
    private final AuthData currUser;
    private final WebSocketFacade ws;

    public GameplayClient(ServerFacade server, Repl repl, AuthData auth, WebSocketFacade ws) {
        this.server = server;
        this.repl = repl;
        this.currUser = auth;
        this.ws = ws;
    }

    @Override
    public String evaluate(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "redraw" -> redrawBoard(params);
//                case "leave" -> leave(params);
//                case "move" -> makeMove(params);
//                case "resign" -> resign(params);
//                case "highlight" -> highlightMoves(params);
//                case "quit" -> "Quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    @Override
    public String help() {
        return """
                Commands you can use:
                - Redraw - redraw the game board
                - Leave - leave the game
                - Move <PIECE POSITION> <POSITION TO MOVE TO> - make a move. Use the format 'a2 a4' to represent the move you want to make
                - Resign - Forfeit the game
                - Highlight <PIECE POSITION> - highlight all legal moves for a given piece. User the format 'a2' to represent the piece.
                - Help - list all possible commands!
                """;
    }
}
