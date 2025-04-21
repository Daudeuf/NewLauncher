package fr.clem76.back;

import com.sun.management.OperatingSystemMXBean;
import fr.clem76.Main;
import fr.clem76.view.MainFrame;
import fr.clem76.view.Options;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.download.DownloadList;
import fr.flowarg.flowupdater.download.IProgressCallback;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Game {
    public static void setupAndStart(MainFrame frame) {
        frame.getProgressbar().setVisible(true);
        frame.setButtonState(false);

        new Thread(() -> {
            try {
                ArrayList<String> mods = Game.setupMods(frame);

                Game.setupLoader(mods, frame);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static ArrayList<String> setupMods(MainFrame frame) throws IOException {
        JSONObject loaded = Main.SAVER.load();

        ArrayList<String> mods = new ArrayList<>();
        ArrayList<String> no_replace = new ArrayList<>();
        ArrayList<String> strict = new ArrayList<>();

        if (!loaded.has("lastZipUrl") || !loaded.getString("lastZipUrl").equals(DataReceiver.data.getString("zipUrl"))) {
            Path tempZip = Files.createTempFile("downloaded-", ".zip");
            try {
                URLConnection connection = new URI(DataReceiver.data.getString("zipUrl")).toURL().openConnection();

                long totalSize = connection.getContentLengthLong();

                try (InputStream in = connection.getInputStream(); OutputStream out = Files.newOutputStream(tempZip, StandardOpenOption.TRUNCATE_EXISTING)) {
                    byte[] buffer = new byte[8192];
                    long downloaded = 0;
                    int bytesRead;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        downloaded += bytesRead;

                        if (totalSize > 0) {
                            frame.getProgressbar().setValue((int) (500.0 * downloaded / totalSize));
                        }
                    }

                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
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

            loaded.put("mods", mods);
            loaded.put("lastZipUrl", DataReceiver.data.getString("zipUrl"));
            Main.SAVER.save(loaded);
        }

        return loaded.getJSONArray("mods").toList().stream().map(obj -> (String) obj).collect(Collectors.toCollection(ArrayList::new));
    }

    private static void setupLoader(ArrayList<String> modsList, MainFrame frame) {
        String mc = DataReceiver.data.getString("minecraft_version");
        String loader = DataReceiver.data.getString("loader_version");

        final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(mc)
                .build();

        try {
            JSONObject json = Main.SAVER.load();
            if (json.has("additional_mods")) {
                modsList.addAll(Arrays.asList(json.getString("additional_mods").split("\n")));
            }
        } catch (IOException _) {}

        String[] mods = modsList.toArray(new String[0]);

        final ForgeVersion forge = new ForgeVersionBuilder()
                .withForgeVersion(String.format("%s-%s", mc, loader))
                .withFileDeleter(new ModFileDeleter(true, mods))
                .build();

        final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(forge)
                .withProgressCallback(new IProgressCallback()
                    {
                        @Override
                        public void update(DownloadList.DownloadInfo info)
                        {
                            frame.getProgressbar().setValue(500 + (int) (500.0 * info.getDownloadedBytes() / info.getTotalToDownloadBytes()));
                        }
                    })
                .build();

        try {
            updater.update(Main.DIRECTORY);

            NoFramework noFramework = new NoFramework(
					Main.DIRECTORY,
					Authentication.getInstance().getAuthInfos(),
					GameFolder.FLOW_UPDATER
			);

			long memorySize = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize() / (1024 * 1024);
            int ramValue = (int) Math.round(Math.min(memorySize, 16384) / 2.0);

            try {
                JSONObject json = Main.SAVER.load();
                if (json.has("ram")) ramValue = json.getInt("ram");
            } catch (IOException _) {}

			noFramework.getAdditionalVmArgs().add( String.format("-Xmx%sM", ramValue) );

			Process p = noFramework.launch(
					updater.getVanillaVersion().getName(),
					loader,
					NoFramework.ModLoader.FORGE
			);

            frame.dispose();

			p.waitFor();

            try {
                JSONObject json = Main.SAVER.load();
                if (json.has("reopen_launcher") && json.getBoolean("reopen_launcher")) {
                    frame.getProgressbar().setVisible(false);
                    frame.setVisible(true);
                    frame.setButtonState(true);
                }
            } catch (IOException _) {}

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
