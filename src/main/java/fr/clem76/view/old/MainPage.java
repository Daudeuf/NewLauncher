package fr.clem76.view.old;

import javax.swing.*;
import java.awt.*;

public class MainPage extends JFrame {
    public MainPage() {
        setTitle("Minecraft Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        // Couleur sombre et moderne
        getContentPane().setBackground(new Color(30, 30, 30));

        // Déconnexion
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setBackground(new Color(255, 69, 58));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 16));
        logoutButton.addActionListener(e -> {
            // Logique de déconnexion ici
        });

        // Jouer
        JButton playButton = new JButton("Jouer");
        playButton.setBackground(new Color(0, 200, 83));
        playButton.setForeground(Color.WHITE);
        playButton.setFont(new Font("Arial", Font.PLAIN, 16));
        playButton.addActionListener(e -> {
            // Logique pour démarrer Minecraft ici
        });

        // Options
        JButton optionsButton = new JButton("Options");
        optionsButton.setBackground(new Color(0, 122, 204));
        optionsButton.setForeground(Color.WHITE);
        optionsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        optionsButton.addActionListener(e -> {
            // Afficher la page des options
            new OptionsPage().setVisible(true);
        });

        // Ajouter les boutons
        add(logoutButton);
        add(playButton);
        add(optionsButton);
    }
}
