package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MoveHistoryPanel extends JPanel {
    private JTextArea currentMovesArea;
    private JTextArea historyArea;

    public MoveHistoryPanel() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Current position panel - using a wider size
        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.setBorder(BorderFactory.createTitledBorder("Current Position"));
        currentMovesArea = new JTextArea();
        currentMovesArea.setEditable(false);
        currentMovesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        currentMovesArea.setLineWrap(true);
        currentMovesArea.setWrapStyleWord(true);
        JScrollPane currentScrollPane = new JScrollPane(currentMovesArea);
        currentScrollPane.setPreferredSize(new Dimension(300, 100));
        currentPanel.add(currentScrollPane);

        // History panel - using a wider size
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Move History"));
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        JScrollPane historyScrollPane = new JScrollPane(historyArea);
        historyScrollPane.setPreferredSize(new Dimension(300, 300));
        historyPanel.add(historyScrollPane);

        add(currentPanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);
    }

    public void updateHistory(List<String> moves) {
        // Clear both areas
        currentMovesArea.setText("");
        historyArea.setText("");

        // Show only the last move in current position with different format
        if (!moves.isEmpty()) {
            String lastMove = moves.get(moves.size() - 1);
            if (lastMove.contains("transformed")) {
                if (moves.size() >= 2) {
                    currentMovesArea.append(moves.get(moves.size() - 2) + "\n");
                }
                currentMovesArea.append(lastMove);
            } else {
                currentMovesArea.append(lastMove);
            }
        }

        // Show full history with original format and numbers
        for (int i = 0; i < moves.size(); i++) {
            historyArea.append(String.format("%d. %s%n", i + 1, moves.get(i)));
        }
    }

    private String formatCurrentPosition(String move) {
        // Keep the format as is, without numbering
        return move;
    }
}