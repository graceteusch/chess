package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    public Collection<ChessMove> calculateChessMoves(ChessPosition start, ChessPosition currPosition, int rowShift, int colShift, Collection<ChessMove> moves, boolean recurse) {
        var currRow = currPosition.getRow();
        var currCol = currPosition.getColumn();
        var newRow = currRow + rowShift;
        var newCol = currCol + colShift;

        // base case: return if an edge is reached
        // later add code for running into another piece
        if (newRow == 0 || newRow == 9 || newCol == 0 || newCol == 9) {
            return moves;
        } else {
            ChessPosition movedPosition = new ChessPosition(newRow, newCol);
            moves.add(new ChessMove(start, movedPosition, null));
            if (recurse) {
                return calculateChessMoves(start, movedPosition, rowShift, colShift, moves, true);
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
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, 1, moves, true));
            // moving towards the upper right diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, -1, moves, true));
            // moving towards the lower left diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, 1, moves, true));
            // moving towards the lower right diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, -1, moves, true));

        } else if (piece.getPieceType() == PieceType.KING) {
            // imagine starting position is 3,3
            // possible moves: 3:2, 3:4, 2:3, 4:3, 4:4, 2:2, 2:4, 4:2
            // so: curr position +/- 1 from the row/col/both unless an edge is reached

            // moving forward
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, 0, moves, false));
            // moving right
            moves.addAll(calculateChessMoves(myPosition, myPosition, 0, 1, moves, false));
            // moving backwards
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, 0, moves, false));
            // moving left
            moves.addAll(calculateChessMoves(myPosition, myPosition, 0, -1, moves, false));

            // moving towards the upper left diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, 1, moves, false));
            // moving towards the upper right diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, -1, moves, false));
            // moving towards the lower left diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, 1, moves, false));
            // moving towards the lower right diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, -1, moves, false));

        } else if (piece.getPieceType() == PieceType.PAWN) {


        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            // moving 2 up and 1 left
            moves.addAll(calculateChessMoves(myPosition, myPosition, 2, -1, moves, false));
            // moving 2 up and 1 right
            moves.addAll(calculateChessMoves(myPosition, myPosition, 2, 1, moves, false));
            // moving 2 right and 1 up
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, 2, moves, false));
            // moving 2 right and 1 down
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, 2, moves, false));
            // moving 2 down and 1 right
            moves.addAll(calculateChessMoves(myPosition, myPosition, -2, 1, moves, false));
            // moving 2 down and 1 left
            moves.addAll(calculateChessMoves(myPosition, myPosition, -2, -1, moves, false));
            // moving 2 left and 1 down
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, -2, moves, false));
            // moving 2 left and 1 up
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, -2, moves, false));

        } else if (piece.getPieceType() == PieceType.QUEEN) {
            // rook moves:
            // moving forward
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, 0, moves, true));
            // moving right
            moves.addAll(calculateChessMoves(myPosition, myPosition, 0, 1, moves, true));
            // moving backwards
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, 0, moves, true));
            // moving left
            moves.addAll(calculateChessMoves(myPosition, myPosition, 0, -1, moves, true));

            // bishop moves:
            // moving towards the upper left diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, 1, moves, true));
            // moving towards the upper right diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, -1, moves, true));
            // moving towards the lower left diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, 1, moves, true));
            // moving towards the lower right diagonal
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, -1, moves, true));


        } else if (piece.getPieceType() == PieceType.ROOK) {
            // moving forward
            moves.addAll(calculateChessMoves(myPosition, myPosition, 1, 0, moves, true));
            // moving right
            moves.addAll(calculateChessMoves(myPosition, myPosition, 0, 1, moves, true));
            // moving backwards
            moves.addAll(calculateChessMoves(myPosition, myPosition, -1, 0, moves, true));
            // moving left
            moves.addAll(calculateChessMoves(myPosition, myPosition, 0, -1, moves, true));
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
