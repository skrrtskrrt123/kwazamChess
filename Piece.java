/**
 * Abstract base class for all chess pieces
 * @author Aleesya
 */
package model;

// Defines common behavior and properties for all chess pieces
public abstract class Piece {
    protected PieceColor color;

    public Piece(PieceColor color) {
        this.color = color;
    }

    public PieceColor getColor() {
        return color;
    }

    public abstract boolean canMove(ChessBoard board, int fromRow, int fromCol, int toRow, int toCol);
    public abstract String getImagePath();
}