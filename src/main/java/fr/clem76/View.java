package fr.clem76;

import javax.swing.*;
import java.awt.*;

public class View {
    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("NewLauncher 1.0");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("Bienvenue dans MonApp !", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 18));
            frame.add(label);

            frame.setVisible(true);
        });
    }
}
