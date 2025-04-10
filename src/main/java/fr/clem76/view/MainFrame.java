package fr.clem76.view;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static fr.clem76.Main.LAUNCHER_LABEL;

public class MainFrame extends JFrame {

    public static final Color BACKGROUND = Color.decode("#1C1C1C");
    public static final Color PRIMARY = Color.decode("#F5E8D8");
    public static final Color ACCENT_1 = Color.decode("#FF6F61");
    public static final Color ACCENT_2 = Color.decode("#DAA520");
    public static final Color HOVER = Color.decode("#FF4500");

    public MainFrame() {
        this.setTitle(LAUNCHER_LABEL);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);

        BorderLayout layout = new BorderLayout();
        this.getContentPane().setBackground(BACKGROUND);
        this.getContentPane().setLayout(layout);

        JLabel label = new JLabel("Bienvenue dans MonApp !", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(PRIMARY);
        this.getContentPane().add(label, BorderLayout.CENTER);

        JButton jouer = new JButton("Jouer");
        jouer.setBackground(ACCENT_1);
        this.getContentPane().add(jouer, BorderLayout.SOUTH);

        JButton option = new JButton("Options");
        option.setBackground(ACCENT_2);
        this.getContentPane().add(option, BorderLayout.EAST);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icon.png")));
        this.setIconImage(icon.getImage());

        this.setVisible(true);
    }

    public static void launch() {
        new MainFrame();
    }
}
