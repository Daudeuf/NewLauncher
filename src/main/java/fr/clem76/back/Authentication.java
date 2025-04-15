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

    private final Saver saveFile;
    private AuthInfos authInfos;


    private Authentication() {
        this.saveFile = new Saver(Main.DIRECTORY.resolve("account-data.json").toFile());
    }

    private void msAuth() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

        try {
            JSONObject loaded = saveFile.load();

            MicrosoftAuthResult response = authenticator.loginWithWebview();

            loaded.put("msAccessToken", response.getAccessToken());
            loaded.put("msRefreshToken", response.getRefreshToken());

            try {
                this.saveFile.save(loaded);

                this.authInfos = new AuthInfos(
                        response.getProfile().getName(),
                        response.getAccessToken(),
                        response.getProfile().getId(),
                        response.getXuid(),
                        response.getClientId()
                );

                // this.ctrl.refreshHeadImg(); // skin refresh
            } catch (IOException _) {}
        } catch (IOException | MicrosoftAuthenticationException _) {}
    }

    private boolean isAuth() {
        try {
            JSONObject loaded = saveFile.load();

            if (loaded.has("msAccessToken") && loaded.has("msRefreshToken")) {
                try
                {
                    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                    MicrosoftAuthResult    response      = authenticator.loginWithRefreshToken(loaded.getString("msRefreshToken"));

                    loaded.put("msAccessToken", response.getAccessToken());
                    loaded.put("msRefreshToken", response.getRefreshToken());
                    this.saveFile.save(loaded);


                    this.authInfos = new AuthInfos(
                            response.getProfile().getName(),
                            response.getAccessToken(),
                            response.getProfile().getId(),
                            response.getXuid(),
                            response.getClientId()
                    );

                    // this.ctrl.refreshHeadImg(); // skin refresh

                    return true;
                }
                catch (MicrosoftAuthenticationException e)
                {
                    loaded.remove("msAccessToken");
                    loaded.remove("msRefreshToken");
                    this.saveFile.save(loaded);
                }
            }


        } catch (IOException _) {}

        return false;
    }

    public AuthInfos getAuthInfos() {
        return authInfos;
    }

    public static void authenticate(Runnable callback) {

        if (instance == null) instance = new Authentication();

        while (!instance.isAuth()) {
            instance.msAuth();
        }

        callback.run();
    }

    public static Authentication getInstance() {
        return instance;
    }
}
