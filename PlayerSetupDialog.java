/**
 * Dialog for player name input
 * @author Aleesya
 */

package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayerSetupDialog extends JDialog {
    private String player1Name = "";
    private String player2Name = "";
    private Image backgroundImage;
    private boolean isConfirmed = false;

    public PlayerSetupDialog(JFrame parent) {
        super(parent, "Player Setup", true);

        backgroundImage = new ImageIcon(getClass().getResource("mainpagebackground.png")).getImage();
        backgroundImage = backgroundImage.getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        
        setLayout(new BorderLayout(10, 10));
        setSize(600, 400);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image to cover the entire panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.add(Box.createVerticalStrut(220));
        
        JPanel bluePlayerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel blueLabel = new JLabel("Blue Player Name:");
        blueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField player1Field = new JTextField(15);
        bluePlayerPanel.add(blueLabel);
        bluePlayerPanel.add(player1Field); // Add text field to the blue panel
        panel.add(bluePlayerPanel);
                
        JPanel redPlayerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel redLabel = new JLabel("Red Player Name:");
        redLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JTextField player2Field = new JTextField(15);
        redPlayerPanel.add(redLabel);
        redPlayerPanel.add(player2Field); // Add text field to the red panel
        panel.add(redPlayerPanel);

        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton okButton = new JButton("Start Game");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            String player1Input = player1Field.getText().trim();
            String player2Input = player2Field.getText().trim();

            if (player1Input.isEmpty() || player2Input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter names for both players", 
                        "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            player1Name = player1Input;
            player2Name = player2Input;
            isConfirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }
}

