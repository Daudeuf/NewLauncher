package fr.clem76;

import fr.clem76.back.Authentication;
import fr.clem76.back.DataReceiver;
import fr.clem76.back.Saver;
import fr.clem76.back.Updater;
import fr.clem76.view.MainFrame;

import java.nio.file.Path;

public class Main {
    public static final String LAUNCHER_NAME = "diamoria";
    public static final String LAUNCHER_LABEL = "Diamoria";
    public static final String LAUNCHER_VERSION = "1.2";
    public static final Path   DIRECTORY = Saver.createMinecraftGameDir(LAUNCHER_NAME, true);
    public static final Saver  SAVER = new Saver(Main.DIRECTORY.resolve("launcher-data.json").toFile());

    public static void main(String[] args) {
        Updater.checkForUpdates();
        DataReceiver.init();
        Authentication.authenticate(MainFrame::launch);
    }
}