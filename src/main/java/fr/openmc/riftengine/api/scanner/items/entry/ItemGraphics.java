package fr.openmc.riftengine.api.scanner.items.entry;

import fr.openmc.core.utils.YmlUtils;
import org.bukkit.Material;

import java.util.Map;

public record ItemGraphics(
        String texture,
        String model,
        Material material
) {
    public static ItemGraphics from(Map<?, ?> data) {
        if (!(data.get("graphics") instanceof Map<?, ?> graphics)) {
            return new ItemGraphics(null, null, Material.PAPER);
        }

        // todo: supporter textures (pour block, ou bow, fishing rod, ect)
        String texture = YmlUtils.getString(graphics.get("texture"), null);
        String model = YmlUtils.getString(graphics.get("model"), null);
        Material material = Material.valueOf(YmlUtils.getString(
                data.get("material"), ItemResource.DEFAULT_MATERIAL).toUpperCase());

        return new ItemGraphics(texture, model, material);
    }

    public boolean hasTexture() {
        if (texture == null) return false;
        return !texture.isEmpty();
    }

    public boolean hasModel() {
        if (model == null) return false;
        return !model.isEmpty();
    }

    public boolean isPresent() {
        return hasTexture() || hasModel();
    }
}