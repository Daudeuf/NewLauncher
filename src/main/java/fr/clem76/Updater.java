package fr.clem76;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Updater {

    private static final String UPDATE_URL = "https://raw.githubusercontent.com/Daudeuf/NewLauncher/refs/heads/master/versions.json";
    private static final String CURRENT_VERSION = "1.0"; // version actuelle de l'app

    public static void checkForUpdates() {
        try {
            JSONObject json = fetchJson(UPDATE_URL);
            String latestVersion = json.getString("version");

            if (!CURRENT_VERSION.equals(latestVersion)) {
                System.out.println("Nouvelle version disponible : " + latestVersion);
                String os = detectOS();
                String downloadUrl = json.getJSONObject("installers").getString(os);

                File installer = downloadInstaller(downloadUrl);
                runInstaller(installer);
            } else {
                System.out.println("Application à jour.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONObject fetchJson(String urlStr) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setRequestProperty("Accept", "application/json");
        try (InputStream in = connection.getInputStream()) {
            String jsonText = new String(in.readAllBytes());
            return new JSONObject(jsonText);
        }
    }

    private static String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "windows";
        if (os.contains("mac")) return "macos";
        if (os.contains("nux")) return "linux";
        throw new UnsupportedOperationException("OS non supporté : " + os);
    }

    private static File downloadInstaller(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        File tempFile = File.createTempFile("installer", getExtension(urlStr));
        tempFile.deleteOnExit();
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    private static String getExtension(String url) {
        if (url.endsWith(".msi")) return ".msi";
        if (url.endsWith(".deb")) return ".deb";
        if (url.endsWith(".dmg")) return ".dmg";
        return ".bin";
    }

    private static void runInstaller(File installer) throws IOException {
        String os = detectOS();

        if (!installer.exists()) {
            throw new FileNotFoundException("Installateur non trouvé : " + installer.getAbsolutePath());
        }

        if (!installer.canExecute()) {
            installer.setExecutable(true);
        }

        System.out.println("Lancement de l’installateur : " + installer.getAbsolutePath());

        ProcessBuilder pb;

        if (os.equals("windows") && installer.getName().endsWith(".msi")) {
            pb = new ProcessBuilder("msiexec", "/i", installer.getAbsolutePath());
        } else {
            pb = new ProcessBuilder(installer.getAbsolutePath());
        }

        pb.inheritIO(); // pour afficher les erreurs
        try {
            pb.start();
        } catch (IOException e) {
            System.err.println("Erreur de lancement de l'installateur : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        System.exit(0);
    }
}
