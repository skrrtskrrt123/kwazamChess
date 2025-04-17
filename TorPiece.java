/**
 * Implementation of the TOR piece movement logic
 * @author Aleesya
 */
package model;

/**
 * TOR piece moves horizontally or vertically any number of squares if path is clear
 */
public class TorPiece extends Piece {
    public TorPiece(PieceColor color) {
        super(color);
    }

    @Override
    public boolean canMove(ChessBoard board, int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }

        int rowDirection = Integer.compare(toRow, fromRow);
        int colDirection = Integer.compare(toCol, fromCol);

        int currentRow = fromRow + rowDirection;
        int currentCol = fromCol + colDirection;

        while (currentRow != toRow || currentCol != toCol) {
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
        return color == PieceColor.RED ? "redTOR.png" : "blueTOR.png";
    }
}