package fr.clem76;

import fr.clem76.back.Authentication;
import fr.clem76.back.Saver;
import fr.clem76.view.MainFrame;

import java.nio.file.Path;

public class Main {
    public static final String LAUNCHER_NAME = "diamoria";
    public static final String LAUNCHER_LABEL = "Diamoria";
    public static final String LAUNCHER_VERSION = "1.0";
    public static final Path   DIRECTORY = Saver.createMinecraftGameDir(LAUNCHER_NAME, true);

    public static void main(String[] args) {
        //Updater.checkForUpdates();

        Authentication.authenticate(() -> {
            MainFrame.launch();
        });
    }
}