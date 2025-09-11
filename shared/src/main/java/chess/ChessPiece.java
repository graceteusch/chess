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

            // moving towards the upper left diagonal
            var row = myPosition.getRow() + 1;
            var col = myPosition.getColumn() + 1;
            while (row != 9 && col != 9) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
                col++;
            }
            // moving towards the upper right diagonal
            row = myPosition.getRow() + 1;
            col = myPosition.getColumn() - 1;
            while (row != 9 && col != 0) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row++;
                col--;
            }
            // moving towards the lower left diagonal
            row = myPosition.getRow() - 1;
            col = myPosition.getColumn() + 1;
            while (row != 0 && col != 9) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
                col++;
            }
            // moving towards the lower right diagonal
            row = myPosition.getRow() - 1;
            col = myPosition.getColumn() - 1;
            while (row != 0 && col != 0) {
                moves.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                row--;
                col--;
            }

        } else if (piece.getPieceType() == PieceType.KING) {
            // imagine starting position is 3,3
            // possible moves: 3:2, 3:4, 2:3, 4:3, 4:4, 2:2, 2:4, 4:2
            // so: curr position +/- 1 from the row/col/both unless an edge is reached

            // loop through all the squares around the king (for -1, 0, 1)
            for (var rowShift = -1; rowShift <= 1; rowShift++) {
                for (var colShift = -1; colShift <= 1; colShift++) {
                    // starts with: row=-1, col=-1
                    // move=2,2

                    // don't include the curr position itself
                    if (rowShift == 0 && colShift == 0) {
                        continue;
                    }

                    // calculate row/col values for the current move
                    var newRow = myPosition.getRow() + rowShift;
                    var newCol = myPosition.getColumn() + colShift;

                    // check for edges
                    if (newRow == 9 || newRow == 0 || newCol == 9 || newCol == 0) {
                        continue;
                    }

                    // add the move
                    moves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                }
            }

        } else if (piece.getPieceType() == PieceType.PAWN) {
            
        } else if (piece.getPieceType() == PieceType.KNIGHT) {

        } else if (piece.getPieceType() == PieceType.QUEEN) {

        } else if (piece.getPieceType() == PieceType.ROOK) {

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
