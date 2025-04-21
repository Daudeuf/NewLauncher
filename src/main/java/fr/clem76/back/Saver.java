package fr.clem76.back;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Saver {
    private final File file;

    public Saver(File file) {
        this.file = file;
        try {
            this.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        if (!file.exists()) {
            save(new JSONObject());
        }
    }

    public void save(JSONObject jsonObject) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonObject.toString(4));
        }
    }

    public JSONObject load() throws IOException {
        String content = Files.readString(file.toPath());
        return new JSONObject(content);
    }

    public static Path createMinecraftGameDir(String serverName, boolean createIfNotExists) {
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        String userHome = System.getProperty("user.home");

        Path dir;

        if (os.contains("win")) {
            dir = Paths.get(System.getenv("APPDATA"), "." + serverName);
        } else if (os.contains("mac")) {
            dir = Paths.get(userHome, "Library", "Application Support", serverName);
        } else {
            dir = Paths.get(userHome, ".local", "share", serverName);
        }

        if (createIfNotExists && !Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return dir;
    }
}

