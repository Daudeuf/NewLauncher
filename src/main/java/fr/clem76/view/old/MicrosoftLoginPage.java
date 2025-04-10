package fr.clem76.view.old;

import javax.swing.*;
import java.awt.*;

public class MicrosoftLoginPage extends JFrame {
    public MicrosoftLoginPage() {
        setTitle("Minecraft Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Couleur sombre et moderne
        getContentPane().setBackground(new Color(30, 30, 30));

        // Création du bouton de connexion
        JButton loginButton = new JButton("Se connecter avec Microsoft");
        loginButton.setBackground(new Color(0, 122, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loginButton.addActionListener(e -> {
            // Logique de connexion Microsoft ici
        });

        // Ajout du bouton à la fenêtre
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.add(loginButton);
        add(panel, BorderLayout.CENTER);
    }
}
