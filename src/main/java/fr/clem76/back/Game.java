package fr.clem76.back;

import fr.clem76.Main;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Game {
    public static void setupAndStart(JProgressBar progressBar) {
        progressBar.setVisible(true);
        progressBar.setValue(50);

        try {
            Game.setup();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setup() throws IOException {
        Path tempZip = Files.createTempFile("downloaded-", ".zip");
        try (InputStream in = new URL(DataReceiver.data.getString("zipUrl")).openStream()) {
            Files.copy(in, tempZip, StandardCopyOption.REPLACE_EXISTING);
        }

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

        Files.walk(tempExtractDir)
            .filter(Files::isRegularFile)
            //.filter(path -> path.getFileName().toString().contains("test")) // filtre de si on copie ou pas
            .forEach(sourceFile -> {
                try {
                    Path relativePath = tempExtractDir.relativize(sourceFile);
                    Path destFile = Main.DIRECTORY.resolve(relativePath);
                    Files.createDirectories(destFile.getParent());
                    Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        Files.walk(tempExtractDir)
            .sorted(Comparator.reverseOrder())
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {}
            });
    }
}
