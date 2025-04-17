/**
 * Implementation of the BIZ piece movement logic
 * @author Aleesya
 */
package model;

//BIZ piece moves in an L-shape pattern (2 squares in one direction, 1 square perpendicular)
public class BizPiece extends Piece {
    public BizPiece(PieceColor color) {
        super(color);
    }

    @Override
    public boolean canMove(ChessBoard board, int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            Piece targetPiece = board.getPiece(toRow, toCol);
            return targetPiece == null || targetPiece.getColor() != this.color;
        }

        return false;
    }

    @Override
    public String getImagePath() {
        return color == PieceColor.RED ? "redBIZ.png" : "blueBIZ.png";
    }
}