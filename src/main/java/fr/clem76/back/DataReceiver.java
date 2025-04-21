package fr.clem76.back;

import fr.clem76.Main;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DataReceiver {
    public static JSONObject data;

    public static void init() {
        try {
            JSONObject loaded = Main.SAVER.load();
            URL url = null;

            while (url == null) {
                String code = null;

                if (loaded.has("url")) {
                    code = loaded.getString("url");
                } else {
                    String base64code = JOptionPane.showInputDialog(null, "Entrez le code du pack :", "Code requis", JOptionPane.PLAIN_MESSAGE);
                    if (base64code != null) {
                        code = decode(base64code);
                    } else {
                        System.exit(0);
                    }
                }

                try {
                    URI uri = new URI(code);
                    if (uri.isAbsolute()) {
                        url = uri.toURL();
                        loaded.put("url", code);
                        Main.SAVER.save(loaded);
                    } else {
                        if (loaded.has("url")) loaded.remove("url");
                        Main.SAVER.save(loaded);
                    }
                } catch (URISyntaxException e) {
                    if (loaded.has("url")) loaded.remove("url");
                    Main.SAVER.save(loaded);
                }
            }

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            try (InputStream in = connection.getInputStream()) {
                data = new JSONObject(new String(in.readAllBytes()));
            } catch (IOException e) {
                if (loaded.has("url")) loaded.remove("url");
                Main.SAVER.save(loaded);
                init();
            }

        } catch (IOException _) {}
    }

    public static String encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.US_ASCII));
    }

    public static String decode(String str) {
        return new String(Base64.getDecoder().decode(str), StandardCharsets.US_ASCII);
    }
}
