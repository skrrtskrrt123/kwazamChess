/**
 * Main controller class implementing MVC pattern for the chess game.
 * @author Aleesya
 */

package controller;
import model.*;
import view.KwazamChessView;
import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import util.SoundPlayer;

//Constructor initializes the game controller
public class KwazamChessController {
    private GameManager gameManager;
    private KwazamChessView view;
    private int selectedRow;
    private int selectedCol;
    private List<Position> validMoves;

    public KwazamChessController() {
        gameManager = new GameManager();
        validMoves = new ArrayList<>();
        selectedRow = -1;
        selectedCol = -1;
    }

    public void setView(KwazamChessView view) {
        this.view = view;
        // Set background music at the very start of the game
        SoundPlayer.playSound("background");
    }

    // Gets move history from game manager
    public List<String> getMoveHistory() {
        return gameManager.getMoveHistory();
    }

    public void startGame() {
        view = new KwazamChessView(this);
        view.setVisible(true);
    }

    public void setPlayerNames(String player1Name, String player2Name) {
        gameManager.setPlayerNames(player1Name, player2Name);
        if (view != null) {
            view.updateStatus(gameManager.getCurrentPlayerName() + "'s turn");
        }
    }

    public String getCurrentPlayerName() {
        return gameManager.getCurrentPlayerName();
    }

    public Piece getPieceAt(int row, int col) {
        return gameManager.getBoard().getPiece(row, col);
    }

    public boolean isValidMovePosition(int row, int col) {
        return validMoves.contains(new Position(row, col));
    }

    // Saves current game state to file
    public void saveGame(String filename) throws IOException {
        gameManager.saveGame(filename);
    }

    // Loads game state from file
    public void loadGame(String filename) throws IOException {
        gameManager.loadGame(filename);
        updateView();
    }

    // Updates game status and view after moves
    private void updateView() {
        if (view != null) {  // Only update if view exists
            view.updateBoard();
            view.updateMoveHistory(gameManager.getMoveHistory());
            updateStatusMessage();
        }
    }

    private void updateStatusMessage() {
        if (view != null) {  // Only update if view exists
            if (gameManager.isGameEnded()) {
                view.updateStatus("Game Over! Winner: " + 
                    (gameManager.getCurrentPlayer() == PieceColor.BLUE ? 
                            gameManager.getPlayer2Name() : gameManager.getPlayer1Name()));
            } else {
                String currentPlayerName = gameManager.getCurrentPlayer() == PieceColor.BLUE ? 
                        gameManager.getPlayer1Name() : gameManager.getPlayer2Name();
                view.updateStatus(currentPlayerName + "'s turn");
            }
        }
    }

    // Handles cell click events for piece selection and movement
    public void onCellClicked(int row, int col) {
        Piece clickedPiece = getPieceAt(row, col);

        if (selectedRow == -1) {
            if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentPlayer()) {
                selectedRow = row;
                selectedCol = col;
                calculateValidMoves(row, col);
                view.updateBoard();
            }
        } else {
            if (isValidMovePosition(row, col)) {
                boolean moved = gameManager.movePiece(selectedRow, selectedCol, row, col);
                if (moved) {
                    clearSelection();
                    updateView();

                    if (gameManager.isGameEnded()) {
                        String winner = gameManager.getCurrentPlayer() == PieceColor.BLUE ? 
                                gameManager.getPlayer2Name() : gameManager.getPlayer1Name();

                        JOptionPane.showMessageDialog(view,
                            "Congratulations " + winner + "!\nYou've won the game!",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                if (clickedPiece != null && clickedPiece.getColor() == gameManager.getCurrentPlayer()) {
                    selectedRow = row;
                    selectedCol = col;
                    calculateValidMoves(row, col);
                } else {
                    clearSelection();
                }
                view.updateBoard();
            }
        }
    }

    private void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        validMoves.clear();
    }

    // Calculates valid moves for selected piece
    private void calculateValidMoves(int row, int col) {
        validMoves.clear();
        Piece piece = getPieceAt(row, col);

        if (piece != null) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 5; c++) {
                    if (gameManager.getBoard().isValidMove(row, col, r, c)) {
                        validMoves.add(new Position(r, c));
                    }
                }
            }
        }
    }

    public boolean isSelected(int row, int col) {
        return row == selectedRow && col == selectedCol;
    }

    public PieceColor getCurrentPlayer() {
        return gameManager.getCurrentPlayer();
    }

    public static class Position {
        private final int row;
        private final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Position)) return false;
            Position other = (Position) obj;
            return this.row == other.row && this.col == other.col;
        }

        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }
}