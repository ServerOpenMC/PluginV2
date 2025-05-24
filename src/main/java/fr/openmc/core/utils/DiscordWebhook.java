package fr.openmc.core.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {

    /**
     * Envoie un message vers un webhook Discord via POST JSON.
     *
     * @param webhookUrl URL complète du webhook (<a href="https://discord.com/api/webhooks/ID/TOKEN">https://discord.com/api/webhooks/ID/TOKEN</a>).
     * @param message    Contenu du message à envoyer.
     * @throws Exception en cas d’erreur de connexion ou d’I/O.
     */
    public static void sendMessage(String webhookUrl, String message) throws Exception {
        URL url = new URI(webhookUrl).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonPayload = String.format("{\"content\":\"%s\"}", message);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status != 204) {
            throw new RuntimeException("Échec du webhook, code HTTP : " + status);
        }
    }
}