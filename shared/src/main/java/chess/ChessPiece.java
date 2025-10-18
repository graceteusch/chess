package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();

        if (getPieceType() == PieceType.PAWN) {
            moves.addAll(calculatePawnMoves(board, myPosition, moves));
        } else if (getPieceType() == PieceType.ROOK) {
            moves.addAll(calculateMoves(1, 0, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(0, 1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(-1, 0, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(0, -1, myPosition, myPosition, board, moves, true));
        } else if (getPieceType() == PieceType.BISHOP) {
            moves.addAll(calculateMoves(1, 1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(-1, 1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(-1, -1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(1, -1, myPosition, myPosition, board, moves, true));
        } else if (getPieceType() == PieceType.QUEEN) {
            // straight (rook)
            moves.addAll(calculateMoves(1, 0, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(0, 1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(-1, 0, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(0, -1, myPosition, myPosition, board, moves, true));
            // diagonal (bishop)
            moves.addAll(calculateMoves(1, 1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(-1, 1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(-1, -1, myPosition, myPosition, board, moves, true));
            moves.addAll(calculateMoves(1, -1, myPosition, myPosition, board, moves, true));
        } else if (getPieceType() == PieceType.KNIGHT) {
            moves.addAll(calculateMoves(2, -1, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(2, 1, myPosition, myPosition, board, moves, false));

            moves.addAll(calculateMoves(1, 2, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(-1, 2, myPosition, myPosition, board, moves, false));

            moves.addAll(calculateMoves(-2, 1, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(-2, -1, myPosition, myPosition, board, moves, false));

            moves.addAll(calculateMoves(-1, -2, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(1, -2, myPosition, myPosition, board, moves, false));
        } else if (getPieceType() == PieceType.KING) {
            // straight
            moves.addAll(calculateMoves(1, 0, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(0, 1, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(-1, 0, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(0, -1, myPosition, myPosition, board, moves, false));
            //diagonal
            moves.addAll(calculateMoves(1, 1, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(-1, 1, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(-1, -1, myPosition, myPosition, board, moves, false));
            moves.addAll(calculateMoves(1, -1, myPosition, myPosition, board, moves, false));
        }
        return moves;
    }

    private Collection<ChessMove> calculateMoves(int rowShift, int colShift, ChessPosition start,
                                                 ChessPosition currPosition, ChessBoard board,
                                                 Collection<ChessMove> moves, boolean recurse) {
        int currRow = currPosition.getRow();
        int currCol = currPosition.getColumn();
        int newRow = currRow + rowShift;
        int newCol = currCol + colShift;

        // base case
        if (newRow >= 9 || newRow <= 0 || newCol >= 9 || newCol <= 0) {
            return moves;
        } else {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece enemy = board.getPiece(newPosition);

            if (!recurse) {
                if (enemy == null || enemy.getTeamColor() != getTeamColor()) {
                    moves.add(new ChessMove(start, newPosition, null));
                }
            } else {
                if (enemy == null) {
                    moves.add(new ChessMove(start, newPosition, null));
                    return calculateMoves(rowShift, colShift, start, newPosition, board, moves, true);
                } else if (enemy.getTeamColor() != getTeamColor()) {
                    moves.add(new ChessMove(start, newPosition, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition pawnPosition, Collection<ChessMove> moves) {
        int currRow = pawnPosition.getRow();
        int currCol = pawnPosition.getColumn();
        int orientation;
        boolean firstMove = false;

        // get orientation
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            orientation = 1;
        } else {
            orientation = -1;
        }

        // create chess position to move one forward
        int newRow = currRow + orientation;
        ChessPosition oneForward = new ChessPosition(newRow, currCol);
        ChessPiece directlyInFront = board.getPiece(oneForward);

        // check for first move
        if (getTeamColor() == ChessGame.TeamColor.WHITE && currRow == 2 || getTeamColor() == ChessGame.TeamColor.BLACK && currRow == 7) {
            firstMove = true;
        }

        // add diagonal moves
        moves.addAll(calculateDiagonalPawnMoves(board, pawnPosition, moves, orientation));

        if (firstMove) {
            ChessPiece twoInFront = board.getPiece(new ChessPosition(newRow + orientation, currCol));

            if (directlyInFront != null) {
                return moves;
            } else if (twoInFront != null) {
                moves.add(new ChessMove(pawnPosition, oneForward, null));
            } else { // move one or two spots forward
                moves.add(new ChessMove(pawnPosition, oneForward, null));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow + orientation, currCol), null));
            }
        } else {
            if (directlyInFront != null) {
                return moves;
            } else if ((getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) || (getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1)) {
                moves.add(new ChessMove(pawnPosition, oneForward, PieceType.QUEEN));
                moves.add(new ChessMove(pawnPosition, oneForward, PieceType.BISHOP));
                moves.add(new ChessMove(pawnPosition, oneForward, PieceType.ROOK));
                moves.add(new ChessMove(pawnPosition, oneForward, PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(pawnPosition, oneForward, null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> calculateDiagonalPawnMoves(ChessBoard board, ChessPosition pawnPosition,
                                                             Collection<ChessMove> moves, int orientation) {
        int currRow = pawnPosition.getRow();
        int currCol = pawnPosition.getColumn();
        int newRow = currRow + orientation;
        ChessPiece leftEnemy;
        ChessPiece rightEnemy;

        // find enemy pieces
        if (currCol == 1) {
            leftEnemy = null;
            rightEnemy = board.getPiece(new ChessPosition(newRow, currCol + 1));
        } else if (currCol == 8) {
            rightEnemy = null;
            leftEnemy = board.getPiece(new ChessPosition(newRow, currCol - 1));
        } else {
            rightEnemy = board.getPiece(new ChessPosition(newRow, currCol + 1));
            leftEnemy = board.getPiece(new ChessPosition(newRow, currCol - 1));
        }

        if (rightEnemy != null && rightEnemy.getTeamColor() != getTeamColor()) {
            if ((getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) || (getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1)) {
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol + 1), PieceType.QUEEN));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol + 1), PieceType.BISHOP));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol + 1), PieceType.ROOK));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol + 1), PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol + 1), null));
            }
        }

        if (leftEnemy != null && leftEnemy.getTeamColor() != getTeamColor()) {
            if ((getTeamColor() == ChessGame.TeamColor.WHITE && newRow == 8) || (getTeamColor() == ChessGame.TeamColor.BLACK && newRow == 1)) {
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol - 1), PieceType.QUEEN));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol - 1), PieceType.BISHOP));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol - 1), PieceType.ROOK));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol - 1), PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(pawnPosition, new ChessPosition(newRow, currCol - 1), null));
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            if (type == PieceType.PAWN) {
                return "P";
            } else if (type == PieceType.BISHOP) {
                return "B";
            } else if (type == PieceType.ROOK) {
                return "R";
            } else if (type == PieceType.KNIGHT) {
                return "N";
            } else if (type == PieceType.KING) {
                return "K";
            } else if (type == PieceType.QUEEN) {
                return "Q";
            }
        } else {
            if (type == PieceType.PAWN) {
                return "p";
            } else if (type == PieceType.BISHOP) {
                return "b";
            } else if (type == PieceType.ROOK) {
                return "r";
            } else if (type == PieceType.KNIGHT) {
                return "n";
            } else if (type == PieceType.KING) {
                return "k";
            } else if (type == PieceType.QUEEN) {
                return "q";
            }
        }
        return "none";
    }
}

