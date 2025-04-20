package fr.clem76.back;

import fr.clem76.Main;
import fr.flowarg.flowupdater.FlowUpdater;
import fr.flowarg.flowupdater.utils.ModFileDeleter;
import fr.flowarg.flowupdater.versions.VanillaVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersion;
import fr.flowarg.flowupdater.versions.forge.ForgeVersionBuilder;
import fr.flowarg.openlauncherlib.NoFramework;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
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
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Game {
    private static final Saver saveFile = new Saver(Main.DIRECTORY.resolve("pack-data.json").toFile());

    public static void setupAndStart(JProgressBar progressBar) {
        progressBar.setVisible(true);
        progressBar.setValue(50);

        try {
            ArrayList<String> mods = Game.setupMods();

            Game.setupLoader(mods);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> setupMods() throws IOException {
        JSONObject loaded = saveFile.load();

        ArrayList<String> mods = new ArrayList<>();
        ArrayList<String> no_replace = new ArrayList<>();
        ArrayList<String> strict = new ArrayList<>();

        if (!loaded.has("lastZipUrl") || !loaded.getString("lastZipUrl").equals(DataReceiver.data.getString("zipUrl"))) {
            Path tempZip = Files.createTempFile("downloaded-", ".zip");
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
            saveFile.save(loaded);
        }

        return loaded.getJSONArray("mods").toList().stream().map(obj -> (String) obj).collect(Collectors.toCollection(ArrayList::new));
    }

    private static void setupLoader(ArrayList<String> modsList) {
        String mc = DataReceiver.data.getString("minecraft_version");
        String loader = DataReceiver.data.getString("loader_version");

        final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName(mc)
                .build();

        /*String modsString = ""; // ctrl.getSaver().get("additional_mod_list", "");
        String[] mods     = modsString.split("\n");*/

        /*System.out.println(mods.length);
        if (mods.length == 0) modsList.addAll(List.of(modsString.split("\n")));
        System.out.println(Arrays.toString(mods));
        System.out.println(modsList);*/

        String[] mods = modsList.toArray(new String[0]);

        final ForgeVersion forge = new ForgeVersionBuilder()
                .withForgeVersion(String.format("%s-%s", mc, loader))
                .withFileDeleter(new ModFileDeleter(true, mods))
                .build();

        final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withModLoaderVersion(forge)
                //.withProgressCallback(callback)
                .build();

        try {
            updater.update(Main.DIRECTORY);

            NoFramework noFramework = new NoFramework(
					Main.DIRECTORY,
					Authentication.getInstance().getAuthInfos(),
					GameFolder.FLOW_UPDATER
			);

			String ramString = "4096"; // ctrl.getSaver().get("ram");
			int    ramValue  = Integer.parseInt(ramString == null ? "4096" : ramString);

            // Set of ram
			noFramework.getAdditionalVmArgs().add( String.format("-Xmx%sM", ramValue) );

			Process p = noFramework.launch(
					updater.getVanillaVersion().getName(),
					loader,
					NoFramework.ModLoader.FORGE
			);

			p.waitFor();

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
