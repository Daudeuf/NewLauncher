package fr.clem76;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private static final String UPDATE_URL = "https://raw.githubusercontent.com/Daudeuf/NewLauncher/refs/heads/master/versions.json";
    private static final String CURRENT_VERSION = "1.1"; // version actuelle de l'app

    public static void checkForUpdates() {
        try {
            JSONObject json = fetchJson();
            String latestVersion = json.getString("version");

            if (!CURRENT_VERSION.equals(latestVersion)) {
                JFrame frame = new JFrame("Mise à jour requise !");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 150);
                frame.setLocationRelativeTo(null);

                JLabel label = new JLabel("Mettez à jour le launcher !");
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);

                JButton btn = new JButton("Ok");
                btn.addActionListener(e -> System.exit(0));

                frame.setLayout(new BorderLayout());
                frame.add(label, BorderLayout.CENTER);
                frame.add(btn, BorderLayout.SOUTH);

                frame.setVisible(true);
            }

        } catch (Exception _) {}
    }

    private static JSONObject fetchJson() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(Updater.UPDATE_URL).openConnection();
        connection.setRequestProperty("Accept", "application/json");
        try (InputStream in = connection.getInputStream()) {
            String jsonText = new String(in.readAllBytes());
            return new JSONObject(jsonText);
        }
    }
}
