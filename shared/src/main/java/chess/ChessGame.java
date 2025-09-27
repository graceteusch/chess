package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor currTeamTurn;
    private ChessPosition whiteKing;
    private ChessPosition blackKing;

    public ChessGame() {
        currTeamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        whiteKing = new ChessPosition(1, 5);
        blackKing = new ChessPosition(8, 5);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // A move is valid if it is a "piece move" for the piece at the input location
        // and making that move would not leave the team’s king in danger of check.

        // maybe - just create a 'version' of the board where the move has been done and then use the isInCheck function?

        var validMoves = new HashSet<ChessMove>();

        // if there is no piece at the start position, return null
        ChessPiece pieceToMove = board.getPiece(startPosition);
        if (pieceToMove == null) {
            return null;
        }

        TeamColor currTeam = pieceToMove.getTeamColor();

        Collection<ChessMove> pieceMoves = pieceToMove.pieceMoves(board, startPosition);
        // for each move in the piece moves
        for (ChessMove move : pieceMoves) {
//            // create a temp duplicated version of the board
//            ChessBoard movedBoard = new ChessBoard();
//            for (int row = 1; row <= 8; row++) {
//                for (int col = 1; col <= 8; col++) {
//                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
//                    movedBoard.addPiece(new ChessPosition(row, col), piece);
//                }
//            }
//            // make the move
//            movedBoard.removePiece(startPosition, pieceToMove);
//            movedBoard.addPiece(move.getEndPosition(), pieceToMove);
//            // after making the move, check whether their team is now in check
//            if (!isInCheck(currTeam)) {
//                validMoves.add(move);
//            }

            // move piece and check if it puts the board into check
            board.removePiece(startPosition, pieceToMove);
            board.addPiece(move.getEndPosition(), pieceToMove);
            if (!isInCheck(currTeam)) {
                validMoves.add(move);
            }

            // put piece back to original spot
            board.removePiece(move.getEndPosition(), pieceToMove);
            board.addPiece(startPosition, pieceToMove);

        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // get piece that is going to be moved (piece at start position)
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        // if there is actually a piece there
        if (pieceToMove != null) {
            // get all the valid moves for that piece
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            // if the move is a valid one
            if (validMoves.contains(move)) {
                // make the move (remove the piece from where it's at and add it to the new position)
                board.removePiece(move.getStartPosition(), pieceToMove);
                board.addPiece(move.getEndPosition(), pieceToMove);
                // if the piece that just moved is a king, update the king's location to keep track
                if (pieceToMove.getPieceType() == ChessPiece.PieceType.KING) {
                    if (pieceToMove.getTeamColor() == TeamColor.WHITE) {
                        whiteKing = move.getEndPosition();
                    } else {
                        blackKing = move.getEndPosition();
                    }
                }
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Check every piece on the board (nested loop)
        // if the piece is NOT NULL, and it's the OPPOSITE COLOR of the teamColor
        // then check if any of their “piece moves” end at the King’s position, if so then the move is invalid

        ChessPosition currTeamsKing = null;
//        if (teamColor == TeamColor.WHITE) {
//            currTeamsKing = whiteKing;
//        } else {
//            currTeamsKing = blackKing;
//        }

        // find king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    currTeamsKing = new ChessPosition(row, col);
                    break;
                }
            }
        }


        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> pieceMoves = piece.pieceMoves(board, new ChessPosition(row, col));
                    for (ChessMove move : pieceMoves) {
                        ChessPosition endPos = move.getEndPosition();
                        if (move.getEndPosition().equals(currTeamsKing)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        } else {
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPiece currPiece = board.getPiece(new ChessPosition(row, col));
                    if (currPiece == null || currPiece.getTeamColor() != teamColor) {
                        continue;
                    }
                    if (validMoves(new ChessPosition(row, col)).isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        System.out.println(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currTeamTurn == chessGame.currTeamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currTeamTurn);
    }
}
