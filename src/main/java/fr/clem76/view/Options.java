package fr.clem76.view;

import com.sun.management.OperatingSystemMXBean;
import fr.clem76.Main;
import fr.clem76.back.Saver;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class Options extends JFrame {
    //public static final Saver saveFile = new Saver(Main.DIRECTORY.resolve("options-data.json").toFile());
    //public static JSONObject json = null;

    /*static {
        try {
            json = saveFile.load();
        } catch (IOException _) {}
    }*/

    private final JSpinner ramSpinner;
    private final JCheckBox reopenLauncherCheckbox;
    private final JTextArea textArea;

    public Options() throws IOException {
        setTitle("Options");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JSONObject json = Main.SAVER.load();

		long memorySize = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize() / (1024 * 1024);
        ramSpinner = new JSpinner(new SpinnerNumberModel(json.has("ram") ? json.getInt("ram") : Math.round(Math.min(memorySize, 16384) / 2.0), 1024, memorySize, 256));
        mainPanel.add(new JLabel(String.format("RAM Disponible : %,d Mo", memorySize)), gbc);
        gbc.gridy = 1;
        mainPanel.add(new JLabel("RAM Alloué :"), gbc);
        gbc.gridx = 1;
        mainPanel.add(ramSpinner, gbc);
        ramSpinner.addChangeListener(_ -> {
            try {
                JSONObject json_temp = Main.SAVER.load();
                json_temp.put("ram", ((Double) ramSpinner.getValue()).intValue());
                Main.SAVER.save(json_temp);
            } catch (IOException _) {}
        });

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        reopenLauncherCheckbox = new JCheckBox("Ouvrir le launcher à la fermeture du jeu");
        mainPanel.add(reopenLauncherCheckbox, gbc);
        gbc.gridwidth = 1;
        reopenLauncherCheckbox.setSelected(!json.has("reopen_launcher") || json.getBoolean("reopen_launcher"));
        reopenLauncherCheckbox.addActionListener(_ -> {
            try {
                JSONObject json_temp = Main.SAVER.load();
                json_temp.put("reopen_launcher", reopenLauncherCheckbox.isSelected());
                Main.SAVER.save(json_temp);
            } catch (IOException _) {}
        });

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Mods additionnels :"), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        textArea = new JTextArea(4, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);
        mainPanel.add(scrollPane, gbc);
        gbc.gridwidth = 1;
        textArea.setText(json.has("additional_mods") ? json.getString("additional_mods") : "");
        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
            try {
                JSONObject json_temp = Main.SAVER.load();
                json_temp.put("additional_mods", textArea.getText());
                Main.SAVER.save(json_temp);
            } catch (IOException _) {}
            }
        });

        gbc.gridy++;
        gbc.gridx = 0;
        JButton recoveryButton = new JButton("Récupération d'instance");
        recoveryButton.addActionListener(this::handleRecovery);
        mainPanel.add(recoveryButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void handleRecovery(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(Main.DIRECTORY.getParent().toFile());
        chooser.setDialogTitle("Sélectionnez un dossier");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            Path selectedPath = chooser.getSelectedFile().toPath();

            if (!selectedPath.equals(Main.DIRECTORY)) {
                ArrayList<String> lst = new ArrayList<>();

                lst.add("journeymap");
                lst.add("saves");
                lst.add("options.txt");

                try {
                    copySelected(selectedPath, Main.DIRECTORY, lst);
                } catch (IOException _) {}
            }
        }
    }

    public static void copySelected(Path origin, Path destination, ArrayList<String> elements) throws IOException {
        for (String name : elements) {
            Path source = origin.resolve(name);
            Path target = destination.resolve(name);

            if (!Files.exists(source)) {
                continue;
            }

            if (Files.isDirectory(source)) {
                Files.walkFileTree(source, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path targetDir = target.resolve(source.relativize(dir));
                        Files.createDirectories(targetDir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path targetFile = target.resolve(source.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                Files.createDirectories(target.getParent()); // au cas où le dossier n'existe pas
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
