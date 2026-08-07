package fr.openmc.core.features.toor.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RequestSigner {

    private static PrivateKey PRIVATE_KEY;

    public static void init(Path pemPath) {
        try {
            String pem = Files.readString(pemPath);
            String cleaned = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(cleaned);
            KeyFactory kf = KeyFactory.getInstance("Ed25519");
            PRIVATE_KEY = kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de lire la cle privee a " + pemPath);
        } catch (Exception e) {
            throw new RuntimeException("Cle privee invalide", e);
        }
    }

    public static String sign(String method, String path, long timestamp, String body) {
        if (PRIVATE_KEY == null) throw new IllegalStateException("RequestSigner non initialisé");
        try {
            String payload = method + "\n" + path + "\n" + timestamp + "\n" + (body == null ? "" : body);
            Signature sig = Signature.getInstance("Ed25519");
            sig.initSign(PRIVATE_KEY);
            sig.update(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sig.sign());
        } catch (Exception e) {
            throw new RuntimeException("Erreur de signature", e);
        }
    }
}
