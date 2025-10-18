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
        board.resetBoard();
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

            // move piece and check if it puts the board into check
            board.removePiece(startPosition, pieceToMove);
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition()); // save the piece that is being captured
            board.addPiece(move.getEndPosition(), pieceToMove);
            if (!isInCheck(currTeam)) {
                validMoves.add(move);
            }

            // put piece back to original spot
            board.removePiece(move.getEndPosition(), pieceToMove);
            board.addPiece(move.getEndPosition(), capturedPiece); // add captured piece back to original spot
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
            // make sure it's the right team's turn
            if (pieceToMove.getTeamColor() != currTeamTurn) {
                // wrong team's turn - throw an exception
                throw new InvalidMoveException("Move provided is NOT VALID.");
            }
            // get all the valid moves for that piece
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            // if the move is a valid one
            if (validMoves.contains(move)) {
                // make the move (remove the piece from where it's at and add it to the new position)
                board.removePiece(move.getStartPosition(), pieceToMove);
                if (move.getPromotionPiece() != null) {
                    board.addPiece(move.getEndPosition(), new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece()));
                } else {
                    board.addPiece(move.getEndPosition(), pieceToMove);
                }

                // if the piece that just moved is a king, update the king's location to keep track
                if (pieceToMove.getPieceType() == ChessPiece.PieceType.KING) {
                    if (pieceToMove.getTeamColor() == TeamColor.WHITE) {
                        whiteKing = move.getEndPosition();
                    } else {
                        blackKing = move.getEndPosition();
                    }
                }

                // change which team's turn it is
                if (currTeamTurn == TeamColor.WHITE) {
                    currTeamTurn = TeamColor.BLACK;
                } else {
                    currTeamTurn = TeamColor.WHITE;
                }
            } else { // invalid move - throw an exception
                throw new InvalidMoveException("Move provided is NOT VALID.");
            }
        } else {  // no piece to move - throw an exception
            throw new InvalidMoveException("Move provided is NOT VALID.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // find king
        ChessPosition currTeamsKing = findKing(teamColor);

        // check every piece on the board (nested loop)
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                // if the piece exists (not null), and it's the OPPOSITE COLOR of the teamColor
                if (!pieceOnSameTeam(teamColor, piece)) {
                    // then check if any of its “piece moves” end at the King’s position - if so then the team IS in check
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

    private ChessPosition findKing(TeamColor teamColor) {
        ChessPosition currTeamsKing = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    currTeamsKing = new ChessPosition(row, col);
                    break;
                }
            }
        }
        return currTeamsKing;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        } else {
            // loop through all pieces of the same team color
            // loop through their valid moves
            // check for isInCheck each time
            // if isInCheck is never false then return true
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition startPos = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(startPos);
                    if (!pieceOnSameTeam(teamColor, piece)) {
                        continue;
                    }

                    Collection<ChessMove> validMoves = validMoves(startPos);
                    for (var move : validMoves) {
                        // move piece and check if it puts the board into check
                        board.removePiece(startPos, piece);
                        board.addPiece(move.getEndPosition(), piece);
                        if (!isInCheck(teamColor)) {
                            return false;
                        }
                        // put piece back to original spot
                        board.removePiece(move.getEndPosition(), piece);
                        board.addPiece(startPos, piece);
                    }
                }
            }
        }
        return true;
    }

    private boolean pieceOnSameTeam(TeamColor teamColor, ChessPiece piece) {
        return piece != null && piece.getTeamColor() == teamColor;
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
                    if (!validMoves(new ChessPosition(row, col)).isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
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
