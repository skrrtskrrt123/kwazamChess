/**
 * Core game logic manager and state handler
 * @author Aleesya
 */

package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import util.SoundPlayer;

public class GameManager {
    private ChessBoard board;
    private PieceColor currentPlayer;
    private int turnCount;
    private int moveCount; 
    private boolean gameEnded;
    private String player1Name;
    private String player2Name;
    private List<String> moveHistory;

    public GameManager() {
        board = new ChessBoard();
        currentPlayer = PieceColor.BLUE;
        moveCount = 0;
        gameEnded = false;
        moveHistory = new ArrayList<>();
    }

    /**
     * Handles piece movement and validates game rules
     * @return true if move was successful, false otherwise
     */
    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (gameEnded) {
            return false;
        }

        Piece piece = board.getPiece(fromRow, fromCol);
        if (piece == null || piece.getColor() != currentPlayer) {
            return false;
        }

        if (!board.isValidMove(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        try {
            // Get target piece before move
            Piece targetPiece = board.getPiece(toRow, toCol);

            // Create move notation
            String pieceName = getPieceName(piece);
            StringBuilder moveNotation = new StringBuilder();
            moveNotation.append(getCurrentPlayerName())
            .append(" moved ")
            .append(pieceName)
            .append(" from ")
            .append((char)('A' + fromCol))
            .append(8 - fromRow)
            .append(" to ")
            .append((char)('A' + toCol))
            .append(8 - toRow);

            if (targetPiece != null) {
                moveNotation.append(" takes ").append(getPieceName(targetPiece));
            }

            // Execute the move
            board.movePiece(fromRow, fromCol, toRow, toCol);

            // Handle RAM piece reaching end of board
            if (piece instanceof RamPiece) {
                ((RamPiece) piece).handleMove(toRow);
            }

            moveHistory.add(moveNotation.toString());
            moveCount++;

            // Play appropriate sound
            if (targetPiece != null) {
                SoundPlayer.playSound("capture");
            } else {
                SoundPlayer.playSound("move");
            }

            // Check if this move captures the opponent's Sau piece
            if (targetPiece instanceof SauPiece && targetPiece.getColor() != currentPlayer) {
                gameEnded = true;
                String winner = (currentPlayer == PieceColor.BLUE) ? player1Name : player2Name;
                moveHistory.add("Game Over! " + winner + " wins!");
                SoundPlayer.playSound("game over");
                switchPlayer();
                return true;
            }

            // Transform pieces after every 4 moves
            if (moveCount % 4 == 0) {
                if (board.transformPieces()) {
                    moveHistory.add("Tor and Xor pieces transformed");
                    SoundPlayer.playSound("transform");
                }
            }

            switchPlayer();
            return true;
        } catch (Exception e) {
            System.err.println("Error during move execution:");
            e.printStackTrace();
            return false;
        }
    }

    // Creates move notation for game history
    private String getPieceName(Piece piece) {
        if (piece instanceof RamPiece) return "Ram";
        if (piece instanceof BizPiece) return "Biz";
        if (piece instanceof SauPiece) return "Sau";
        if (piece instanceof TorPiece) return "Tor";
        if (piece instanceof XorPiece) return "Xor";
        return "Unknown";
    }

    public void loadGame(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Reset the game state first
            gameEnded = false;
            moveCount = 0;
            moveHistory.clear();

            // Read basic game info
            player1Name = reader.readLine();
            player2Name = reader.readLine();
            currentPlayer = PieceColor.valueOf(reader.readLine());
            turnCount = Integer.parseInt(reader.readLine());

            // Create clean board
            board = new ChessBoard();
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 5; col++) {
                    board.placePiece(row, col, null);
                }
            }

            String line;
            boolean readingHistory = false;

            while ((line = reader.readLine()) != null) {
                if (line.equals("HISTORY_START")) {
                    readingHistory = true;
                    continue;
                }

                if (readingHistory) {
                    moveHistory.add(line);
                    if (!line.contains("transformed")) {
                        moveCount++;
                    }
                    if (line.startsWith("Game Over")) {
                        gameEnded = true;
                    }
                } else {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        int row = Integer.parseInt(parts[0]);
                        int col = Integer.parseInt(parts[1]);
                        String pieceType = parts[2];
                        PieceColor color = PieceColor.valueOf(parts[3]);

                        Piece piece = null;
                        switch (pieceType) {
                            case "RamPiece":
                                RamPiece ramPiece = new RamPiece(color);
                                if ((color == PieceColor.RED && row == 7) ||
                                (color == PieceColor.BLUE && row == 0)) {
                                    ramPiece.setHasReachedEnd(true);
                                    ramPiece.setMovingForward(false);
                                }
                                piece = ramPiece;
                                break;
                            case "TorPiece": piece = new TorPiece(color); break;
                            case "XorPiece": piece = new XorPiece(color); break;
                            case "BizPiece": piece = new BizPiece(color); break;
                            case "SauPiece": piece = new SauPiece(color); break;
                        }
                        if (piece != null) {
                            board.placePiece(row, col, piece);
                        }
                    }
                }
            }
        }
    }

    /**
     * Saves current game state to file
     * @throws IOException if file cannot be written
     */
    public void saveGame(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(player1Name);
            writer.println(player2Name);
            writer.println(currentPlayer);
            writer.println(turnCount);

            // Save board state
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 5; col++) {
                    Piece piece = board.getPiece(row, col);
                    if (piece != null) {
                        writer.printf("%d,%d,%s,%s%n", row, col, 
                            piece.getClass().getSimpleName(), piece.getColor());
                    }
                }
            }

            // Save move history
            writer.println("HISTORY_START");
            for (String move : moveHistory) {
                writer.println(move);
            }
        }
    }

    public void setPlayerNames(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == PieceColor.BLUE) ? PieceColor.RED : PieceColor.BLUE;
    }

    // Getter methods
    public boolean isGameEnded() {
        return gameEnded;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public PieceColor getCurrentPlayer() {
        return currentPlayer;
    }

    public String getCurrentPlayerName() {
        return currentPlayer == PieceColor.BLUE ? player1Name : player2Name;
    }

    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }
}