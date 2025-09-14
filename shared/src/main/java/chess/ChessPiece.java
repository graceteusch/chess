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

    public boolean checkIfFirstMove(ChessPiece pawn, ChessPosition pawnPosition) {
        if (pawn.getTeamColor() == ChessGame.TeamColor.WHITE && pawnPosition.getRow() == 2) {
            return true;
        } else if (pawn.getTeamColor() == ChessGame.TeamColor.BLACK && pawnPosition.getRow() == 7) {
            return true;
        } else {
            return false;
        }
    }

    public HashSet<ChessMove> findPawnEnemies(ChessBoard board, ChessPiece pawn, ChessPosition pawnPosition) {
        int currRow = pawnPosition.getRow();
        int currCol = pawnPosition.getColumn();
        HashSet<ChessMove> diagonalMoves = new HashSet<ChessMove>();

        // get color (diagonal moves are opposites depending on color)
        ChessGame.TeamColor pawnColor = pawn.getTeamColor();

        // create vars for right and left places
        ChessPiece pawnEnemyRight;
        ChessPiece pawnEnemyLeft;

        // find enemies based on color
        if (pawnColor == ChessGame.TeamColor.WHITE) {
            // only get one diagonal if pawn is on the edge
            if (currCol == 8) {
                pawnEnemyRight = null;
                pawnEnemyLeft = board.getPiece(new ChessPosition(currRow + 1, currCol - 1));
            } else if (currCol == 1) {
                pawnEnemyRight = board.getPiece(new ChessPosition(currRow + 1, currCol + 1));
                pawnEnemyLeft = null;
            } else {
                pawnEnemyRight = board.getPiece(new ChessPosition(currRow + 1, currCol + 1));
                pawnEnemyLeft = board.getPiece(new ChessPosition(currRow + 1, currCol - 1));
            }
            // add valid diagonal moves to list
            if (pawnEnemyRight != null && pawnEnemyRight.getTeamColor() != pawn.getTeamColor()) {
                diagonalMoves.add(new ChessMove(pawnPosition, new ChessPosition(currRow + 1, currCol + 1), null));
            }
            if (pawnEnemyLeft != null && pawnEnemyLeft.getTeamColor() != pawn.getTeamColor()) {
                diagonalMoves.add(new ChessMove(pawnPosition, new ChessPosition(currRow + 1, currCol - 1), null));
            }
        } else {
            // only get one diagonal if pawn is on the edge
            if (currCol == 8) {
                pawnEnemyRight = null;
                pawnEnemyLeft = board.getPiece(new ChessPosition(currRow - 1, currCol - 1));
            } else if (currCol == 1) {
                pawnEnemyRight = board.getPiece(new ChessPosition(currRow - 1, currCol + 1));
                pawnEnemyLeft = null;
            } else {
                pawnEnemyRight = board.getPiece(new ChessPosition(currRow - 1, currCol + 1));
                pawnEnemyLeft = board.getPiece(new ChessPosition(currRow - 1, currCol - 1));
            }
            // add valid diagonal moves to list
            if (pawnEnemyRight != null && pawnEnemyRight.getTeamColor() != pawn.getTeamColor()) {
                diagonalMoves.add(new ChessMove(pawnPosition, new ChessPosition(currRow - 1, currCol + 1), null));
            }
            if (pawnEnemyLeft != null && pawnEnemyLeft.getTeamColor() != pawn.getTeamColor()) {
                diagonalMoves.add(new ChessMove(pawnPosition, new ChessPosition(currRow - 1, currCol - 1), null));
            }
        }
        return diagonalMoves;
    }

    public Collection<ChessMove> calculateChessMoves(
            ChessBoard board, ChessPosition start, ChessPosition currPosition,
            int rowShift, int colShift, Collection<ChessMove> moves, boolean recurse) {

        var currRow = currPosition.getRow();
        var currCol = currPosition.getColumn();
        var newRow = currRow + rowShift;
        var newCol = currCol + colShift;
        ChessPiece myPiece = board.getPiece(start);
        ChessPosition movedPosition = new ChessPosition(newRow, newCol);
        ChessPiece enemy = board.getPiece(movedPosition);

        if (myPiece.getPieceType() == PieceType.PAWN) {
            // check if there is a piece directly in front of the pawn
            ChessPiece pieceDirectlyInFront;
            if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                pieceDirectlyInFront = board.getPiece(new ChessPosition(currRow + 1, currCol));
            } else {
                pieceDirectlyInFront = board.getPiece(new ChessPosition(currRow - 1, currCol));
            }
            // add normal forward move (as long as there isn't a piece directly in front)
            if (enemy == null && pieceDirectlyInFront == null) {
                moves.add(new ChessMove(start, movedPosition, null));
            }

            // add diagonal moves if an enemy is present
            moves.addAll(findPawnEnemies(board, myPiece, currPosition));
            // return all moves
            return moves;
        }

        // base case: return if an edge is reached
        // later add code for running into another piece
        if (newRow <= 0 || newRow >= 9 || newCol <= 0 || newCol >= 9) {
            return moves;
        } else {

            if (!recurse && (enemy == null || enemy.getTeamColor() != myPiece.getTeamColor())) {
                // no recursion, move to the square if it is empty or if the enemy piece is opposite color
                moves.add(new ChessMove(start, movedPosition, null));
                return moves;
            } else if (enemy == null) {
                // recursion - square is empty so continue on normally with recursion
                moves.add(new ChessMove(start, movedPosition, null));
                return calculateChessMoves(board, start, movedPosition, rowShift, colShift, moves, true);
            } else {
                // recursive, but the square is NOT empty, so check if it is opposite color and can therefore be captured
                if (enemy.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(new ChessMove(start, movedPosition, null));
                    return moves;
                }
            }
            return moves;
        }
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);

        var moves = new HashSet<ChessMove>();

        if (piece.getPieceType() == PieceType.BISHOP) {
            // the bishop can move in a diagonal line as far as possible
            // so: it's curr position +/- the same amount on both the row/col until an edge is reached
            // start with curr position +/- 1 on both the row/col

            // moving towards the upper left diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 1, moves, true));
            // moving towards the upper right diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, -1, moves, true));
            // moving towards the lower left diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 1, moves, true));
            // moving towards the lower right diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, -1, moves, true));

        } else if (piece.getPieceType() == PieceType.KING) {
            // imagine starting position is 3,3
            // possible moves: 3:2, 3:4, 2:3, 4:3, 4:4, 2:2, 2:4, 4:2
            // so: curr position +/- 1 from the row/col/both unless an edge is reached

            // moving forward
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 0, moves, false));
            // moving right
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 0, 1, moves, false));
            // moving backwards
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 0, moves, false));
            // moving left
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 0, -1, moves, false));

            // moving towards the upper left diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 1, moves, false));
            // moving towards the upper right diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, -1, moves, false));
            // moving towards the lower left diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 1, moves, false));
            // moving towards the lower right diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, -1, moves, false));

        } else if (piece.getPieceType() == PieceType.PAWN) {
            // add a check for pawns to the calculator function to check diagonally for enemies

            // get pawn's color
            ChessGame.TeamColor pawnColor = piece.getTeamColor();
            // check if it's the pawn's first move
            boolean firstMove = checkIfFirstMove(piece, myPosition);
            if (firstMove && pawnColor == ChessGame.TeamColor.WHITE) {
                // if first move, move forward 2 (OR move forward 1)
                moves.addAll(calculateChessMoves(board, myPosition, myPosition, 2, 0, moves, false));
            } else if (firstMove && pawnColor == ChessGame.TeamColor.BLACK) {
                // if first move, move forward 2 (OR move forward 1)
                moves.addAll(calculateChessMoves(board, myPosition, myPosition, -2, 0, moves, false));
            }
            // move forward 1 (no matter if it's the first turn or not)
            if (pawnColor == ChessGame.TeamColor.WHITE) {
                moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 0, moves, false));
            } else {
                moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 0, moves, false));
            }


        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            // moving 2 up and 1 left
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 2, -1, moves, false));
            // moving 2 up and 1 right
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 2, 1, moves, false));
            // moving 2 right and 1 up
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 2, moves, false));
            // moving 2 right and 1 down
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 2, moves, false));
            // moving 2 down and 1 right
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -2, 1, moves, false));
            // moving 2 down and 1 left
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -2, -1, moves, false));
            // moving 2 left and 1 down
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, -2, moves, false));
            // moving 2 left and 1 up
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, -2, moves, false));

        } else if (piece.getPieceType() == PieceType.QUEEN) {
            // rook moves:
            // moving forward
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 0, moves, true));
            // moving right
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 0, 1, moves, true));
            // moving backwards
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 0, moves, true));
            // moving left
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 0, -1, moves, true));

            // bishop moves:
            // moving towards the upper left diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 1, moves, true));
            // moving towards the upper right diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, -1, moves, true));
            // moving towards the lower left diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 1, moves, true));
            // moving towards the lower right diagonal
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, -1, moves, true));


        } else if (piece.getPieceType() == PieceType.ROOK) {
            // moving forward
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 1, 0, moves, true));
            // moving right
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 0, 1, moves, true));
            // moving backwards
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, -1, 0, moves, true));
            // moving left
            moves.addAll(calculateChessMoves(board, myPosition, myPosition, 0, -1, moves, true));
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
}
