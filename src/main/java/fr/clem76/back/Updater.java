package fr.clem76.back;

import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import static fr.clem76.Main.LAUNCHER_VERSION;

public class Updater {
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/Daudeuf/NewLauncher/refs/heads/master/versions.json";

    public static void checkForUpdates() {
        try {
            JSONObject json = fetchJson();
            String latestVersion = json.getString("version");

            if (!LAUNCHER_VERSION.equals(latestVersion)) {
                JOptionPane.showMessageDialog(
                    null,
                    "Désinstallez le launcher et installez la nouvelle version du launcher",
                    "Mise à jour requise",
                    JOptionPane.WARNING_MESSAGE
                );

                System.exit(0);
            }
        } catch (Exception _) {}
    }

    private static JSONObject fetchJson() throws IOException, URISyntaxException {
        HttpURLConnection connection = (HttpURLConnection) new URI(Updater.UPDATE_URL).toURL().openConnection();
        connection.setRequestProperty("Accept", "application/json");
        try (InputStream in = connection.getInputStream()) {
            String jsonText = new String(in.readAllBytes());
            return new JSONObject(jsonText);
        }
    }
}
