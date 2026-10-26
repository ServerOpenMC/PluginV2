package fr.openmc.riftengine.api.scanner.icons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.openmc.core.utils.JsonUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class OffsetCaracterScanner extends AbstractScanner<Set<Character>, Path> {
    private final String TARGET_FILE = "assets/minecraft/font/default.json";

    public Set<Character> scan(Path javaRootPath) {
        Set<Character> offsetCaracters = new HashSet<>();

        JsonObject jsonObject = JsonUtils.loadJson(javaRootPath.resolve(TARGET_FILE));
        for (JsonElement elt : jsonObject.getAsJsonArray("providers")) {
            JsonObject provider = elt.getAsJsonObject();

            if (!(provider.has("file") &&
                    JsonUtils.getString(provider, "file", "e").equals("char/black.png"))) continue;

            JsonArray chars = provider.getAsJsonArray("chars");

            for (JsonElement charElt : chars) {
                offsetCaracters.add(charElt.getAsString().charAt(0));
            }
        }

        return offsetCaracters;
    }
}
