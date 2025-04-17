/**
 * Main view class for the chess game GUI
 * @author Aleesya
 */

package view;

import model.*;
import controller.KwazamChessController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class KwazamChessView extends JFrame {
    //Initializes board display with flip capability
    private static final int ROWS = 8;
    private static final int COLS = 5;
    private static final int CELL_SIZE = 80;
    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 0); 
    private static final Color SELECTED_COLOR = new Color(173, 216, 230);

    private JPanel boardPanel;
    private JLabel statusLabel;
    private KwazamChessController controller;
    private MoveHistoryPanel historyPanel;
    private JPanel controlPanel;
    private boolean isFlipped = false;

    public KwazamChessView(KwazamChessController controller) {
        this.controller = controller;
        controller.setView(this);

        setTitle("Kwazam Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Initialize components first
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initializeComponents(mainPanel);
        add(mainPanel);

        // Then show player setup dialog
        PlayerSetupDialog setup = new PlayerSetupDialog(this);
        setup.setVisible(true);
        if (!setup.isConfirmed()) {
            System.exit(0);
        }

        // Set player names after UI components are initialized
        controller.setPlayerNames(setup.getPlayer1Name(), setup.getPlayer2Name());

        pack();
        setLocationRelativeTo(null);
    }

    private void initializeComponents(JPanel mainPanel) {
        // Create status panel
        statusLabel = new JLabel(controller.getCurrentPlayerName() + "'s turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(statusLabel, BorderLayout.NORTH);

        // Create board panel
        boardPanel = new JPanel(new GridLayout(ROWS, COLS));
        boardPanel.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Create control panel
        controlPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save Game");
        JButton loadButton = new JButton("Load Game");

        saveButton.addActionListener(e -> saveGame());
        loadButton.addActionListener(e -> loadGame());

        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Add move history panel
        historyPanel = new MoveHistoryPanel();
        mainPanel.add(historyPanel, BorderLayout.EAST);

        add(mainPanel);
        initializeBoard();
        pack();
        setLocationRelativeTo(null);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
                    int choice = JOptionPane.showConfirmDialog(
                            this,
                            "Start a new game?",
                            "New Game",
                            JOptionPane.YES_NO_OPTION
                        );

                    if (choice == JOptionPane.YES_OPTION) {
                        // Create new controller and update the view reference
                        KwazamChessController newController = new KwazamChessController();
                        this.controller = newController;
                        newController.setView(this);  // Set the view reference in the new controller

                        PlayerSetupDialog setup = new PlayerSetupDialog(this);
                        setup.setVisible(true);
                        if (setup.isConfirmed()) {
                            newController.setPlayerNames(setup.getPlayer1Name(), setup.getPlayer2Name());
                            updateBoard();
                            historyPanel.updateHistory(newController.getMoveHistory());
                            updateStatus(newController.getCurrentPlayerName() + "'s turn");
                        }
                    }
            });

        controlPanel.add(newGameButton, 0);
    }

    private void initializeBoard() {
        boardPanel.removeAll();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int row = isFlipped ? (ROWS - 1 - i) : i;
                int col = isFlipped ? (COLS - 1 - j) : j;

                JPanel cell = createCellPanel(row, col);
                Piece piece = controller.getPieceAt(row, col);

                if (piece != null) {
                    try {
                        ImageIcon icon = new ImageIcon(getClass().getResource("/chessImages/" + piece.getImagePath()));
                        Image img = icon.getImage();
                        if (img != null) {
                            if (isFlipped) {
                                img = createFlippedImage(img);
                            }
                            img = img.getScaledInstance(CELL_SIZE - 10, CELL_SIZE - 10, Image.SCALE_SMOOTH);
                            JLabel pieceLabel = new JLabel(new ImageIcon(img));
                            pieceLabel.setHorizontalAlignment(JLabel.CENTER);
                            cell.add(pieceLabel);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + piece.getImagePath());
                    }
                }

                boardPanel.add(cell);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private JPanel createCellPanel(int row, int col) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        if (controller.isSelected(row, col)) {
            cell.setBackground(SELECTED_COLOR);
        } else if (controller.isValidMovePosition(row, col)) {
            cell.setBackground(HIGHLIGHT_COLOR);
        } else {
            cell.setBackground((row + col) % 2 == 0 ? new Color(212,186,149) : new Color(153,99,54));
        }

        final int finalRow = row;
        final int finalCol = col;
        cell.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    controller.onCellClicked(finalRow, finalCol);
                }
            });

        return cell;
    }

    private Image createFlippedImage(Image image) {
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);

        // Check for valid dimensions
        if (width <= 0 || height <= 0) {
            return image;
        }

        BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = flippedImage.createGraphics();

        g.translate(width, height);
        g.scale(-1, -1);
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return flippedImage;
    }

    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.saveGame(fileChooser.getSelectedFile().getPath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving game: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controller.loadGame(fileChooser.getSelectedFile().getPath());
                updateBoard();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading game: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void updateBoard() {
        // Flip the board automatically based on current player
        isFlipped = controller.getCurrentPlayer() == PieceColor.RED;
        initializeBoard();
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    //Updates move history display
    public void updateMoveHistory(List<String> moves) {
        historyPanel.updateHistory(moves);
    }
}