package fr.openmc.riftengine.api.scanner.sounds;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.openmc.core.utils.FilesUtils;
import fr.openmc.core.utils.JsonUtils;
import fr.openmc.riftengine.api.registry.scanner.AbstractScanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Scan tout les sons initialisé par ItemsAdder
 */
public class SoundScanner extends AbstractScanner<List<SoundEntry>, Path> {
    @Override
    public List<SoundEntry> scan(Path javaRootPath) {
        List<SoundEntry> result = new ArrayList<>();
        Path assetsPath = javaRootPath.resolve("assets");

        for (String namespace : FilesUtils.listFolderNames(assetsPath.toString())) {
            Path file = assetsPath.resolve(namespace).resolve("sounds.json");
            if (!Files.exists(file)) continue;

            JsonObject root = JsonUtils.loadJson(file);
            if (root == null) continue;

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                SoundEntry soundEntry = parseEntry(namespace, entry.getKey(), entry.getValue());
                if (soundEntry != null) result.add(soundEntry);
            }
        }
        return result;
    }

    private SoundEntry parseEntry(String namespace, String eventId, JsonElement element) {
        if (!(element instanceof JsonObject eventObj)) return null;
        if (!(eventObj.get("sounds") instanceof JsonArray soundsArray)) return null;

        List<String> soundResourceId = new ArrayList<>();
        for (JsonElement elt : soundsArray) {
            if (!(elt instanceof JsonObject jsonObject)) continue;

            soundResourceId.add(JsonUtils.getString(jsonObject, "name", null));
        }
        return soundResourceId.isEmpty() ? null : new SoundEntry(namespace, eventId, soundResourceId);
    }
}
