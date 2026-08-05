package fr.openmc.core.features.discord;

import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.features.discord.utils.RequestSigner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class InternalBotApiClient {

    public record LinkStatus(boolean linked, String discordUserId) {
        public static final LinkStatus NOT_LINKED = new LinkStatus(false, null);
    }

    public static LinkStatus checkLinkStatus(String code) {
        try {
            HttpURLConnection con = open("/internal/link/status/" + code, "GET", null);
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            if (con.getResponseCode() != 200) {
                con.disconnect();
                return LinkStatus.NOT_LINKED;
            }

            JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            con.disconnect();

            if (!Boolean.TRUE.equals(json.get("linked"))) return LinkStatus.NOT_LINKED;
            return new LinkStatus(true, (String) json.get("discordUserId"));
        } catch (Exception e) {
            OMCLogger.warn("Impossible de contacter l'API interne du bot (link status): {}", e.getMessage(), e);
            return LinkStatus.NOT_LINKED;
        }
    }

    public static void consumeCode(String code) {
        try {
            HttpURLConnection con = open("/internal/link/status/" + code, "DELETE", null);
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.getResponseCode();
            con.disconnect();
        } catch (Exception e) {
            OMCLogger.warn("Impossible de nettoyer le code de liaison: {}", e.getMessage(), e);
        }
    }

    private static HttpURLConnection open(String path, String method, String body) throws Exception {
        long timestamp = System.currentTimeMillis();
        String signature = RequestSigner.sign(method, path, timestamp, body);

        HttpURLConnection con = (HttpURLConnection) new URI(DiscordLinkManager.getBotUrl() + path).toURL().openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("x-timestamp", String.valueOf(timestamp));
        con.setRequestProperty("x-signature", signature);
        con.setRequestProperty("User-Agent", "OpenMC-Plugin");
        return con;
    }

    public record LinkRequestResult(boolean success, String code) {
        public static final LinkRequestResult FAILED = new LinkRequestResult(false, null);
    }

    public static LinkRequestResult requestLinkCode(UUID uuid, String username) {
        try {
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("uuid", uuid.toString());
            bodyJson.put("username", username);
            String bodyStr = bodyJson.toJSONString();

            HttpURLConnection con = open("/internal/link/request", "POST", bodyStr);
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

            try (var os = con.getOutputStream()) {
                os.write(bodyJson.toJSONString().getBytes(StandardCharsets.UTF_8));
            }

            if (con.getResponseCode() != 200) {
                con.disconnect();
                return LinkRequestResult.FAILED;
            }

            JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            con.disconnect();
            String code = (String) json.get("code");
            return code == null ? LinkRequestResult.FAILED : new LinkRequestResult(true, code);
        } catch (Exception e) {
            OMCLogger.warn("Impossible de contacter l'API interne du bot (link request): {}", e.getMessage(), e);
            return LinkRequestResult.FAILED;
        }
    }

    public static boolean notifyUnlink(UUID uuid) {
        try {
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("uuid", uuid.toString());
            String bodyStr = bodyJson.toJSONString();

            HttpURLConnection con = open("/internal/link/unlink", "POST", bodyStr);
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

            try (var os = con.getOutputStream()) {
                os.write(bodyStr.getBytes(StandardCharsets.UTF_8));
            }

            boolean ok = con.getResponseCode() == 200;
            con.disconnect();
            return ok;
        } catch (Exception e) {
            OMCLogger.warn("Impossible de notifier le bot du unlink: {}", e.getMessage(), e);
            return false;
        }
    }

}
