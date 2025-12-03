package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;

import static ui.EscapeSequences.*;

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
        System.out.print(RESET_TEXT_COLOR);
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
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
            System.out.println(error.getMessage());
        }
        System.out.print(RESET_TEXT_COLOR);

        repl.printPrompt();
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
            System.out.print(RESET_TEXT_COLOR);
            return "You left the game.";
        } else {
            System.out.print(RESET_TEXT_COLOR);
            System.out.println("Invalid input");
            throw new ServerResponseException("To leave a game, please use the following format: leave");
        }
    }

    private String makeMove(String[] params) {
        if (color == null) {
            System.out.print(RESET_TEXT_COLOR);
            return "You cannot make moves as an observer.";
        }

        if (params.length == 3) {
            // pawn promotion
            String currPiecePosition = params[0];
            String newPosition = params[1];
            String promotionPiece = params[2];

            ChessPosition currPos = getChessPosition(currPiecePosition);
            ChessPosition newPos = getChessPosition(newPosition);

            // check if this is actually a valid pawn promotion move
            if ((currPos.getRow() == 2 && newPos.getRow() == 1) || (currPos.getRow() == 7 && newPos.getRow() == 8)) {
                ChessPiece.PieceType promotionType;
                if (promotionPiece.equalsIgnoreCase("queen")) {
                    promotionType = ChessPiece.PieceType.QUEEN;
                } else if (promotionPiece.equalsIgnoreCase("rook")) {
                    promotionType = ChessPiece.PieceType.ROOK;
                } else if (promotionPiece.equalsIgnoreCase("bishop")) {
                    promotionType = ChessPiece.PieceType.BISHOP;
                } else if (promotionPiece.equalsIgnoreCase("knight")) {
                    promotionType = ChessPiece.PieceType.KNIGHT;
                } else {
                    throw new ServerResponseException("Please provide a valid promotion piece type (queen/rook/bishop/knight)");
                }

                // make the promotion piece move
                ChessMove move = new ChessMove(currPos, newPos, promotionType);

                ws.makeMove(gameID, currUser, move);
                return "";
            } else {
                throw new ServerResponseException("This is not a valid pawn promotion move.");
            }
        }

        if (params.length == 2) {
            String currPiecePosition = params[0];
            String newPosition = params[1];

            ChessPosition currPos = getChessPosition(currPiecePosition);
            ChessPosition newPos = getChessPosition(newPosition);

            ChessMove move = new ChessMove(currPos, newPos, null);

            ws.makeMove(gameID, currUser, move);

            return "";
            //return String.format("You made the move" + params[0] + " " + params[1] + ".");
        } else {
            System.out.print(RESET_TEXT_COLOR);
            System.out.println("Invalid input");
            throw new ServerResponseException("To make a move, use the following format: a2 a4 (for example)");
        }
    }

    private ChessPosition getChessPosition(String input) {
        if (input.length() != 2) {
            System.out.print(RESET_TEXT_COLOR);
            System.out.println("Invalid input");
            throw new ServerResponseException("To make a move, use the following format: a2 a4 (for example)");
        }
        char colChar = input.charAt(0);
        char rowChar = input.charAt(1);

        // check if col char can be converted to a valid number
        int col = colToInt(colChar);

        // check if row char is a valid number
        int row;
        try {
            row = Integer.parseInt(String.valueOf(rowChar));
        } catch (NumberFormatException ex) {
            System.out.print(RESET_TEXT_COLOR);
            System.out.println("Invalid input");
            throw new ServerResponseException("To make a move, use the following format: a2 a4 (for example)");
        }
        if (row < 1 || row > 8) {
            System.out.print(RESET_TEXT_COLOR);
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
            System.out.print(RESET_TEXT_COLOR);
            return "You cannot resign as an observer.";
        }

        if (params.length == 0) {
            System.out.print(SET_TEXT_COLOR_BLUE);
            System.out.println("Are you sure you want to resign? Type yes or no.");
            System.out.print(RESET_TEXT_COLOR);
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String result = line.trim();

            if (!result.equalsIgnoreCase("yes")) {
                System.out.print(SET_TEXT_COLOR_BLUE);
                return "Not resigning.";
            }

            ws.resign(gameID, currUser);
            System.out.print(SET_TEXT_COLOR_BLUE);
            return "You have resigned from the game.";
        } else {
            System.out.print(RESET_TEXT_COLOR);
            System.out.println("Invalid input");
            throw new ServerResponseException("To resign from a game, please use the following format: resign");
        }
    }


    private String highlightMoves(String[] params) {
        if (params.length == 1) {
            ChessPosition position = getChessPosition(params[0]);
            if (currGame.getBoard().getPiece(position) == null) {
                throw new ServerResponseException("There is no piece at that position.");
            }
            Collection<ChessMove> legalMoves = currGame.validMoves(position);
            ArrayList<ChessPosition> squaresToHighlight = new ArrayList<>();
            squaresToHighlight.add(position);
            for (ChessMove move : legalMoves) {
                squaresToHighlight.add(move.getEndPosition());
            }
            boolean whitePerspective;
            if (color == null || color == ChessGame.TeamColor.WHITE) {
                whitePerspective = true;
            } else {
                whitePerspective = false;
            }

            BoardDrawer.drawPerspective(currGame.getBoard(), whitePerspective, squaresToHighlight);
            return "";
        } else {
            System.out.print(RESET_TEXT_COLOR);
            System.out.println("Invalid input");
            throw new ServerResponseException("To highlight valid moves, please use the following format: highlight a2");
        }
    }


    @Override
    public String help() {
        return """
                Commands you can use:
                - Redraw - redraw the game board
                - Leave - leave the game
                - Move <PIECE POSITION> <POSITION TO MOVE TO> - make a move. Use the format 'a2 a4' to represent the move you want to make.
                  If moving a pawn to the end of the board, include <PROMOTION PIECE> (queen/rook/bishop/knight).
                - Resign - Forfeit the game
                - Highlight <PIECE POSITION> - highlight all legal moves for a given piece. Use the format 'a2' to represent the piece.
                - Help - list all possible commands
                """;
    }
}
