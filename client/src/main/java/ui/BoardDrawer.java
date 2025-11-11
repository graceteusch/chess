package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static chess.ChessPiece.PieceType.*;
import static ui.EscapeSequences.*;

public class BoardDrawer {

    public static void drawBoard(ChessBoard board) {
        System.out.print(ERASE_SCREEN);
        System.out.println("  a  b  c  d  e  f  g  h  ");
        for (int i = 8; i >= 1; i--) {
            System.out.print(i);
            for (int j = 1; j <= 8; j++) {
                var pieceSymbol = getUnicodeSymbol(board.getPiece(new ChessPosition(i, j)));
                System.out.print(pieceSymbol);

            }
            System.out.println();
        }
        System.out.println("  a  b  c  d  e  f  g  h  ");
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
