package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    public static void drawBoard(ChessBoard board, ChessGame.TeamColor team) {
        System.out.print(ERASE_SCREEN);
        if (team.equals(ChessGame.TeamColor.WHITE)) {
            drawWhitePerspective(board);
        } else {
            drawBlackPerspective(board);
        }
    }

    private static void drawWhitePerspective(ChessBoard board) {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        System.out.println("\n   a  b  c  d  e  f  g  h  ");
        for (int i = 8; i >= 1; i--) {
            System.out.print(RESET_BG_COLOR);
            System.out.print(SET_TEXT_COLOR_LIGHT_GREY);

            System.out.print(i + " ");
            for (int j = 1; j <= 8; j++) {
                boolean isLightSquare = (i + j) % 2 != 0;
                if (isLightSquare) {
                    System.out.print(SET_BG_COLOR_LIGHT_BLUE);
                    var pieceSymbol = getUnicodeSymbol(board.getPiece(new ChessPosition(i, j)));
                    System.out.print(pieceSymbol);
                } else {
                    System.out.print(SET_BG_COLOR_DARK_BLUE);
                    var pieceSymbol = getUnicodeSymbol(board.getPiece(new ChessPosition(i, j)));
                    System.out.print(pieceSymbol);
                }
                System.out.print(RESET_BG_COLOR);
            }
            System.out.println();
        }
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        System.out.println("  a  b  c  d  e  f  g  h  ");
    }

    private static void drawBlackPerspective(ChessBoard board) {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        System.out.println("   h  g  f  e  d  c  b  a  ");
        for (int i = 1; i <= 8; i++) {
            System.out.print(RESET_BG_COLOR);
            System.out.print(SET_TEXT_COLOR_LIGHT_GREY);

            System.out.print(i + " ");
            for (int j = 8; j >= 1; j--) {
                boolean isLightSquare = (i + j) % 2 != 0;
                if (isLightSquare) {
                    System.out.print(SET_BG_COLOR_LIGHT_BLUE);
                    var pieceSymbol = getUnicodeSymbol(board.getPiece(new ChessPosition(i, j)));
                    System.out.print(pieceSymbol);
                } else {
                    System.out.print(SET_BG_COLOR_DARK_BLUE);
                    var pieceSymbol = getUnicodeSymbol(board.getPiece(new ChessPosition(i, j)));
                    System.out.print(pieceSymbol);
                }
                System.out.print(RESET_BG_COLOR);
            }
            System.out.println();
        }
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        System.out.println("   h  g  f  e  d  c  b  a  ");
    }

    private static String getUnicodeSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        var type = piece.getPieceType();
        var color = piece.getTeamColor();
        if (color == ChessGame.TeamColor.WHITE) {
            return switch (type) {
                case PAWN -> WHITE_PAWN;
                case ROOK -> WHITE_ROOK;
                case KNIGHT -> WHITE_KNIGHT;
                case BISHOP -> WHITE_BISHOP;
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
            };
        } else {
            return switch (type) {
                case PAWN -> BLACK_PAWN;
                case ROOK -> BLACK_ROOK;
                case KNIGHT -> BLACK_KNIGHT;
                case BISHOP -> BLACK_BISHOP;
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
            };
        }

    }


}
