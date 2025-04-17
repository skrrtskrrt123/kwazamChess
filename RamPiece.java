/**
 * Implementation of the RAM piece movement logic
 * @author Aleesya
 */

package model;

public class RamPiece extends Piece {
    private boolean isMovingForward;
    private boolean hasReachedEnd;

    public RamPiece(PieceColor color) {
        super(color);
        this.isMovingForward = true;
        this.hasReachedEnd = false;
    }

    /**
     * Validates RAM piece movement rules:
     * - Moves forward until reaching end
     * - Then moves backward after reaching end
     * - Stays in same column
     */
    @Override
    public boolean canMove(ChessBoard board, int fromRow, int fromCol, int toRow, int toCol) {
        // Must stay in same column
        if (fromCol != toCol) {
            return false;
        }

        // Get movement direction based on color and current direction
        int direction = getMovementDirection();

        // Validate movement is one step in correct direction
        if (toRow - fromRow != direction) {
            return false;
        }

        // Check if target position is empty or contains enemy piece
        Piece targetPiece = board.getPiece(toRow, toCol);
        if (targetPiece != null && targetPiece.getColor() == this.color) {
            return false;
        }

        // Check if the piece will reach the end with this move
        boolean willReachEnd = (color == PieceColor.RED && toRow == 7) ||
            (color == PieceColor.BLUE && toRow == 0);

        // Only allow movement in these cases:
        // 1. Moving forward (not reached end yet)
        // 2. Already reached end AND moving backward
        if (isMovingForward && !hasReachedEnd) {
            if (willReachEnd) {
                // Will automatically set hasReachedEnd and flip direction after move
                return true;
            }
            return true;
        } else if (!isMovingForward && hasReachedEnd) {
            return true;
        }

        return false;
    }

    // Called after a successful move to handle end-of-board logic
    // Basically updates piece state after reaching board end
    public void handleMove(int newRow) {
        if (!hasReachedEnd && isMovingForward) {
            // Check if this move reached the end
            if ((color == PieceColor.RED && newRow == 7) ||
            (color == PieceColor.BLUE && newRow == 0)) {
                hasReachedEnd = true;
                isMovingForward = false;
            }
        }
    }

    private int getMovementDirection() {
        if (color == PieceColor.RED) {
            return isMovingForward ? 1 : -1;
        } else {
            return isMovingForward ? -1 : 1;
        }
    }

    @Override
    public String getImagePath() {
        if (color == PieceColor.RED) {
            return hasReachedEnd ? "redRAM_flipped.png" : "redRAM.png";
        } else {
            return hasReachedEnd ? "blueRAM_flipped.png" : "blueRAM.png";
        }
    }

    // Getters and setters
    public boolean isMovingForward() {
        return isMovingForward;
    }

    public boolean hasReachedEnd() {
        return hasReachedEnd;
    }

    public void setMovingForward(boolean forward) {
        this.isMovingForward = forward;
    }

    public void setHasReachedEnd(boolean reached) {
        this.hasReachedEnd = reached;
    }
}