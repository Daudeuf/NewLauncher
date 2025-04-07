package fr.clem76;

import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Updater {
    private static final String VERSION_URL = "https://raw.githubusercontent.com/Daudeuf/NewLauncher/refs/heads/master/versions.json"; // change-moi
    private static final String CURRENT_VERSION = "1.0";

    public static void checkForUpdateAndMaybeRun() {
        try {
            JSONObject versionData = fetchVersionData();
            String latestVersion = versionData.getString("version");
            String changelog = versionData.getString("changelog");

            String os = System.getProperty("os.name").toLowerCase();
            String downloadUrl;
            if (os.contains("win")) {
                downloadUrl = versionData.getJSONObject("urls").getString("windows");
            } else if (os.contains("mac")) {
                downloadUrl = versionData.getJSONObject("urls").getString("mac");
            } else {
                downloadUrl = versionData.getJSONObject("urls").getString("linux");
            }

            if (!CURRENT_VERSION.equals(latestVersion)) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Nouvelle version disponible : " + latestVersion + "\n\n" + changelog + "\n\nMettre à jour ?",
                        "Mise à jour disponible",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    Path file = downloadFile(downloadUrl);
                    Runtime.getRuntime().exec(file.toAbsolutePath().toString());
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de mise à jour : " + e.getMessage());
        }
    }

    private static JSONObject fetchVersionData() throws IOException {
        URL url = new URL(VERSION_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        try (InputStream is = conn.getInputStream()) {
            String json = new String(is.readAllBytes());
            return new JSONObject(json);
        }
    }

    private static Path downloadFile(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        Path target = Paths.get(System.getProperty("java.io.tmpdir"), new File(fileUrl).getName());
        try (InputStream in = url.openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target;
    }
}
