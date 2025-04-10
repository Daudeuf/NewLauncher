package fr.clem76;

import fr.clem76.back.Authentication;
import fr.clem76.view.MainFrame;

public class Main {
    public static final String LAUNCHER_NAME = "diamoria";
    public static final String LAUNCHER_LABEL = "Diamoria";
    public static final String LAUNCHER_VERSION = "1.0";

    public static void main(String[] args) {
        //Updater.checkForUpdates();

        Authentication.authenticate(() -> {
            MainFrame.launch();
        });
    }
}