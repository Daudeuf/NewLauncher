package fr.clem76.back;

import fr.clem76.Main;
import org.json.JSONObject;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Game {
    private static final Saver saveFile = new Saver(Main.DIRECTORY.resolve("pack-data.json").toFile());

    public static void setupAndStart(JProgressBar progressBar) {
        progressBar.setVisible(true);
        progressBar.setValue(50);

        try {
            ArrayList<String> mods = Game.setup();

            for (String m : mods) System.out.println(m);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> setup() throws IOException {
        Path tempZip = Files.createTempFile("downloaded-", ".zip");
        //try (InputStream in = new URL(DataReceiver.data.getString("zipUrl")).openStream()) {
        try (InputStream in = new URI(DataReceiver.data.getString("zipUrl")).toURL().openStream()) {
            Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException _) {}

        Path tempExtractDir = Files.createTempDirectory("zip-extract-");

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZip.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;

                Path extractedFile = tempExtractDir.resolve(entry.getName()).normalize();
                Files.createDirectories(extractedFile.getParent());
                Files.copy(zis, extractedFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        Files.deleteIfExists(tempZip);

        ArrayList<String> mods = new ArrayList<>();
        ArrayList<String> no_replace = new ArrayList<>();
        ArrayList<String> strict = new ArrayList<>();

        for (Object o : DataReceiver.data.getJSONArray("filesRules")) {
            JSONObject json = (JSONObject) o;

            if ("strict".equals(json.getString("rule"))) {
                strict.add(json.getString("file"));
            } else {
                no_replace.add(json.getString("file"));
            }
        }

        for (String s: strict) {
            deleteDirectory(Main.DIRECTORY.resolve(s));
        }

        Files.walk(tempExtractDir)
            .filter(Files::isRegularFile)
            .filter(path -> {
                Path relativePath = tempExtractDir.relativize(path);
                boolean start = false;
                for (String s: no_replace)
                    if (relativePath.startsWith(s)) {
                        start = true;
                        break;
                    }
                return !Main.DIRECTORY.resolve(relativePath).toFile().exists() || !start;
            })
            .forEach(sourceFile -> {
                try {
                    Path relativePath = tempExtractDir.relativize(sourceFile);
                    if (relativePath.startsWith("mods")) mods.add(sourceFile.getFileName().toString());
                    Path destFile = Main.DIRECTORY.resolve(relativePath);
                    Files.createDirectories(destFile.getParent());
                    Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException _) {}
            });

        deleteDirectory(tempExtractDir);

        return mods;
    }

    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException _) {}
                });
        }
    }
}
