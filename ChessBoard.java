/**
 * Core chess board implementation managing piece placement and movement validation
 * @author Aleesya
   */

package model;

/**
 * Manages the core board operations and piece placement
 */
public class ChessBoard {
    private static final int ROWS = 8;
    private static final int COLS = 5;
    private Piece[][] board;

    public ChessBoard() {
        board = new Piece[ROWS][COLS];
        initializeBoard();
    }

    /**
     * Initializes the chess board with starting piece positions
     */
    private void initializeBoard() {
        // Initialize the board with pieces
        // Red pieces
        board[0][0] = new TorPiece(PieceColor.RED);
        board[0][1] = new BizPiece(PieceColor.RED);
        board[0][2] = new SauPiece(PieceColor.RED);
        board[0][3] = new BizPiece(PieceColor.RED);
        board[0][4] = new XorPiece(PieceColor.RED);
        for (int col = 0; col < COLS; col++) {
            board[1][col] = new RamPiece(PieceColor.RED);
        }

        // Blue pieces
        board[7][0] = new XorPiece(PieceColor.BLUE);
        board[7][1] = new BizPiece(PieceColor.BLUE);
        board[7][2] = new SauPiece(PieceColor.BLUE);
        board[7][3] = new BizPiece(PieceColor.BLUE);
        board[7][4] = new TorPiece(PieceColor.BLUE);
        for (int col = 0; col < COLS; col++) {
            board[6][col] = new RamPiece(PieceColor.BLUE);
        }
    }

    /**
     * Handles piece transformation logic for Tor and Xor pieces
     * @return true if any pieces were transformed, false otherwise
     */
    public boolean transformPieces() {
        boolean transformed = false;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Piece piece = board[row][col];
                if (piece instanceof TorPiece) {
                    board[row][col] = new XorPiece(piece.getColor());
                    transformed = true;
                } else if (piece instanceof XorPiece) {
                    board[row][col] = new TorPiece(piece.getColor());
                    transformed = true;
                }
            }
        }
        return transformed;
    }

    public Piece getPiece(int row, int col) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            return board[row][col];
        }
        return null;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (isValidPosition(fromRow, fromCol) && isValidPosition(toRow, toCol)) {
            board[toRow][toCol] = board[fromRow][fromCol];
            board[fromRow][fromCol] = null;
        }
    }

    /**
     * Validates piece movement based on game rules
     */
    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) {
            return false;
        }

        Piece piece = board[fromRow][fromCol];
        if (piece == null) {
            return false;
        }

        return piece.canMove(this, fromRow, fromCol, toRow, toCol);
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    public void placePiece(int row, int col, Piece piece) {
        board[row][col] = piece;
    }
}