/**
 * Implementation of the XOR piece movement logic
 * @author Aleesya
 */
package model;

// XOR piece moves diagonally any number of squares if path is clear
public class XorPiece extends Piece {
    public XorPiece(PieceColor color) {
        super(color);
    }

    @Override
    public boolean canMove(ChessBoard board, int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if (rowDiff != colDiff) {
            return false;
        }

        int rowDirection = Integer.compare(toRow, fromRow);
        int colDirection = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowDirection;
        int currentCol = fromCol + colDirection;

        while (currentRow != toRow && currentCol != toCol) {
            if (board.getPiece(currentRow, currentCol) != null) {
                return false;
            }
            currentRow += rowDirection;
            currentCol += colDirection;
        }

        Piece targetPiece = board.getPiece(toRow, toCol);
        return targetPiece == null || targetPiece.getColor() != this.color;
    }

    @Override
    public String getImagePath() {
        return color == PieceColor.RED ? "redXOR.png" : "blueXOR.png";
    }
}