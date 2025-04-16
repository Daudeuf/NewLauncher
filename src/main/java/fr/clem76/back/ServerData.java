package fr.clem76.back;

import fr.clem76.Main;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ServerData {
    private static final File SERVER_DAT = Main.DIRECTORY.resolve("servers.dat").toFile();

    public static void init(String address, List<String> oldAddress) throws IOException {
        CompoundTag root;
        ListTag<CompoundTag> serverList;

        if (!SERVER_DAT.exists()) {
            serverList = new ListTag<>(CompoundTag.class);
            CompoundTag server = new CompoundTag();
            server.putString("name", Main.LAUNCHER_LABEL);
            server.putString("ip", address);
            serverList.add(server);

            root = new CompoundTag();
            root.put("servers", serverList);

            NBTUtil.write(new NamedTag(null, root), SERVER_DAT, false);
        } else {
            NamedTag named = NBTUtil.read(SERVER_DAT);

            root = (CompoundTag) named.getTag();

            serverList = (ListTag<CompoundTag>) root.getListTag("servers");

            boolean modified = false;
            for (CompoundTag server : serverList) {
                String ip = server.getString("ip");

                for (String oldIp : oldAddress) {
                    if (ip.equals(oldIp)) {
                        server.putString("ip", address);
                        modified = true;
                    }
                }
            }

            if (modified) {
                NBTUtil.write(new NamedTag(null, root), SERVER_DAT, false);
            }
        }
    }
}
