/**
 * Implementation of the SAU piece movement logic (king-like piece)
 * @author Aleesya
 */
package model;

/**
 * SAU piece moves one square in any direction (horizontal, vertical, or diagonal)
 * Capturing this piece ends the game
 */
public class SauPiece extends Piece {
    public SauPiece(PieceColor color) {
        super(color);
    }

    @Override
    public boolean canMove(ChessBoard board, int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff <= 1 && colDiff <= 1) {
            Piece targetPiece = board.getPiece(toRow, toCol);
            return targetPiece == null || targetPiece.getColor() != this.color;
        }

        return false;
    }

    @Override
    public String getImagePath() {
        return color == PieceColor.RED ? "redSAU.png" : "blueSAU.png";
    }
}