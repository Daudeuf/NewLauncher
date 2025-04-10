package fr.clem76.view.old;

import javax.swing.*;
import java.awt.*;

public class OptionsPage extends JFrame {
    public OptionsPage() {
        setTitle("Options Minecraft Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        // Couleur sombre et moderne
        getContentPane().setBackground(new Color(30, 30, 30));

        // Choix de la RAM
        JPanel ramPanel = new JPanel();
        ramPanel.setBackground(new Color(30, 30, 30));
        ramPanel.add(new JLabel("Choisissez la RAM allouée :"));
        JComboBox<String> ramComboBox = new JComboBox<>(new String[] {"2GB", "4GB", "6GB", "8GB"});
        ramComboBox.setBackground(Color.WHITE);
        ramPanel.add(ramComboBox);

        // Ajouter un TextField pour les mods personnalisés
        JPanel modsPanel = new JPanel();
        modsPanel.setBackground(new Color(30, 30, 30));
        modsPanel.add(new JLabel("Liste des mods personnalisés (séparés par des virgules) :"));
        JTextField modsField = new JTextField(20);
        modsPanel.add(modsField);

        // Ajouter un bouton pour importer un profil
        JButton importProfileButton = new JButton("Importer un profil");
        importProfileButton.setBackground(new Color(0, 122, 204));
        importProfileButton.setForeground(Color.WHITE);
        importProfileButton.addActionListener(e -> {
            // Logique pour importer le profil ici
        });

        // Ajouter les panels et le bouton à la fenêtre
        add(ramPanel);
        add(modsPanel);
        add(importProfileButton);
    }
}
