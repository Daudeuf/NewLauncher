package fr.clem76.back;

import fr.clem76.Main;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import org.json.JSONObject;

import java.io.IOException;

public class Authentication {
    private static Authentication instance;

    private AuthInfos authInfos;

    private void msAuth() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

        try {
            JSONObject loaded = Main.SAVER.load();

            MicrosoftAuthResult response = authenticator.loginWithWebview();

            if (response == null) return;

            loaded.put("msAccessToken", response.getAccessToken());
            loaded.put("msRefreshToken", response.getRefreshToken());

            try {
                Main.SAVER.save(loaded);

                this.authInfos = new AuthInfos(
                        response.getProfile().getName(),
                        response.getAccessToken(),
                        response.getProfile().getId(),
                        response.getXuid(),
                        response.getClientId()
                );
            } catch (IOException _) {}
        } catch (IOException | MicrosoftAuthenticationException _) {}
    }

    private boolean isAuth() {
        try {
            JSONObject loaded = Main.SAVER.load();

            if (loaded.has("msAccessToken") && loaded.has("msRefreshToken")) {
                try
                {
                    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                    MicrosoftAuthResult    response      = authenticator.loginWithRefreshToken(loaded.getString("msRefreshToken"));

                    loaded.put("msAccessToken", response.getAccessToken());
                    loaded.put("msRefreshToken", response.getRefreshToken());
                    Main.SAVER.save(loaded);


                    this.authInfos = new AuthInfos(
                            response.getProfile().getName(),
                            response.getAccessToken(),
                            response.getProfile().getId(),
                            response.getXuid(),
                            response.getClientId()
                    );

                    return true;
                }
                catch (MicrosoftAuthenticationException e)
                {
                    loaded.remove("msAccessToken");
                    loaded.remove("msRefreshToken");
                    Main.SAVER.save(loaded);
                }
            }
        } catch (IOException _) {}

        return false;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public void disconnect() {
        this.authInfos = null;

        try {
            JSONObject loaded = Main.SAVER.load();
            loaded.remove("msAccessToken");
            loaded.remove("msRefreshToken");
            Main.SAVER.save(loaded);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void authenticate(Runnable callback) {

        if (instance == null) instance = new Authentication();

        if (!instance.isAuth()) {
            instance.msAuth();
        }

        if (instance.isAuth()) callback.run();
    }

    public static Authentication getInstance() {
        return instance;
    }
}
