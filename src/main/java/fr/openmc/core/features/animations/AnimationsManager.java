package fr.openmc.core.features.animations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.animations.listeners.EmoteListener;
import fr.openmc.core.features.animations.listeners.PlayerFinishJoiningListener;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class AnimationsManager {

    public AnimationsManager() {
        OMCPlugin plugin = OMCPlugin.getInstance();

        saveAllAnimation(plugin);

        loadAllAnimations(plugin);

        OMCPlugin.registerEvents(
                new EmoteListener(),
                new PlayerFinishJoiningListener()
        );
    }

    public JsonObject loadAnimation(OMCPlugin plugin, String ressourcePath) {
        File file = new File(plugin.getDataFolder(), ressourcePath);
        if (!file.exists()) {
            return null;
        }

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadAllAnimations(OMCPlugin plugin) {
        for (Animation animation : Animation.values()) {
            String animationName = animation.getNameAnimation();

            String resourcePath = "data/animations/" + animationName + ".animation.json";
            JsonObject animationJson = loadAnimation(plugin, resourcePath);

            JsonObject animations = animationJson.getAsJsonObject("animations");
            JsonObject animationObject = animations.getAsJsonObject(animationName);
            animation.setTotalTicks((int) Math.round(animationObject.get("animation_length").getAsDouble() * 20));

            JsonObject bones = animationObject.getAsJsonObject("bones");

            JsonObject cameraPos = bones.getAsJsonObject("camera_pos").getAsJsonObject("position");
            for (Map.Entry<String, JsonElement> entry : cameraPos.entrySet()) {
                int tick = (int) Math.round(Double.parseDouble(entry.getKey()) * 20);
                JsonArray arr = entry.getValue().getAsJsonArray();
                animation.cameraPositions.put(tick,
                        new Vector(
                                arr.get(0).getAsDouble() / 16.0,
                                arr.get(1).getAsDouble() / 16.0,
                                arr.get(2).getAsDouble() / 16.0
                        )
                );
            }

            JsonObject cameraView = bones.getAsJsonObject("camera_view").getAsJsonObject("position");
            for (Map.Entry<String, JsonElement> entry : cameraView.entrySet()) {
                int tick = (int) Math.round(Double.parseDouble(entry.getKey()) * 20); // ex. 0.05s en tick
                JsonArray arr = entry.getValue().getAsJsonArray();
                animation.cameraViews.put(tick, new Vector(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble()));
            }
        }
    }

    public void saveAllAnimation(OMCPlugin plugin) {
        for (Animation animation : Animation.values()) {
            String animationJsonName = animation.getNameAnimation();

            String resourcePath = "data/animations/" + animationJsonName + ".animation.json";
            saveDefaultAnimation(plugin, resourcePath);
        }
    }

    public void saveDefaultAnimation(OMCPlugin plugin, String resourcePath) {
        File folder = new File(plugin.getDataFolder(), "data/animations");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File targetFile = new File(plugin.getDataFolder(), resourcePath);
        if (!targetFile.exists()) {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) {
                    return;
                }

                Files.copy(in, targetFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
