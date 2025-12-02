package ui;

import chess.*;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    public static void drawBoard(ChessBoard board, ChessGame.TeamColor team) {
        System.out.print(ERASE_SCREEN);
        if (team.equals(ChessGame.TeamColor.WHITE)) {
            drawPerspective(board, true, null);
        } else {
            drawPerspective(board, false, null);
        }
    }


    public static void drawPerspective(ChessBoard board, boolean whitePerspective, Collection<ChessPosition> highlightSquares) {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        String colNames;
        int rowStart;
        int rowEnd;
        int rowIncrease;

        if (whitePerspective) {
            colNames = "\n   a  b  c  d  e  f  g  h  ";
            rowStart = 8;
            rowEnd = 1;
            rowIncrease = -1;
        } else {
            colNames = "\n   a  b  c  d  e  f  g  h  ";
            rowStart = 1;
            rowEnd = 8;
            rowIncrease = 1;
        }

        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        System.out.println(colNames);
        for (int i = rowStart; i >= rowEnd; i += rowIncrease) {
            System.out.print(RESET_BG_COLOR);
            System.out.print(SET_TEXT_COLOR_LIGHT_GREY);

            System.out.print(i + " ");
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPos = new ChessPosition(i, j);
                boolean highlight = false;
                if (highlightSquares != null && highlightSquares.contains(currPos)) {
                    highlight = true;
                }

                boolean isLightSquare = (i + j) % 2 != 0;
                if (isLightSquare) {
                    if (highlight) {
                        System.out.print(SET_BG_COLOR_LIGHT_YELLOW);
                    } else {
                        System.out.print(SET_BG_COLOR_LIGHT_BLUE);
                    }
                    var pieceSymbol = getUnicodeSymbol(board.getPiece(new ChessPosition(i, j)));
                    System.out.print(pieceSymbol);
                } else {
                    if (highlight) {
                        System.out.print(SET_BG_COLOR_DARK_YELLOW);
                    } else {
                        System.out.print(SET_BG_COLOR_DARK_BLUE);
                    }
                    var pieceSymbol = getUnicodeSymbol(board.getPiece(new ChessPosition(i, j)));
                    System.out.print(pieceSymbol);
                }
                System.out.print(RESET_BG_COLOR);
            }
            System.out.println();
        }
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY);
        System.out.println(colNames);

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
