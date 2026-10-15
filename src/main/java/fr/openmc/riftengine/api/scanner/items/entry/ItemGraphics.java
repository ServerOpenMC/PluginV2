package fr.openmc.riftengine.api.scanner.items.entry;

import fr.openmc.core.utils.YmlUtils;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public record ItemGraphics(
        String texture,
        Map<String, String> textures,
        String model,
        Material material
) {
    public static ItemGraphics from(Map<?, ?> data) {
        if (!(data.get("graphics") instanceof Map<?, ?> graphics))
            return new ItemGraphics(null, null, null, Material.PAPER);


        Map<String, String> textures = null;

        Object texturesObj = graphics.get("textures");
        if (texturesObj instanceof Map<?, ?> texturesMap) {
            textures = new HashMap<>();

            for (Map.Entry<?, ?> entry : texturesMap.entrySet()) {
                textures.put(
                        entry.getKey().toString(),
                        entry.getValue().toString()
                );
            }
        }

        String texture = YmlUtils.getString(graphics.get("texture"), null);
        String model = YmlUtils.getString(graphics.get("model"), null);
        Material material = Material.valueOf(YmlUtils.getString(
                data.get("material"), ItemResource.DEFAULT_MATERIAL).toUpperCase());

        return new ItemGraphics(texture, textures, model, material);
    }

    public boolean hasTexture() {
        if (texture == null) return false;
        return !texture.isEmpty();
    }

    public boolean hasTextures() {
        if (textures == null) return false;
        return !textures.isEmpty();
    }

    public boolean hasModel() {
        if (model == null) return false;
        return !model.isEmpty();
    }

    public boolean isPresent() {
        return hasTexture() || hasModel();
    }
}