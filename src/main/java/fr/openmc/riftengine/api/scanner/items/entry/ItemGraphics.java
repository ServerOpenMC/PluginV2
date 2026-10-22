package fr.openmc.riftengine.api.scanner.items.entry;

import fr.openmc.core.utils.YmlUtils;
import org.bukkit.Material;

import java.util.Map;

public record ItemGraphics(
        String texture,
        Map<String, String> textures,
        String model,
        String parent, // ex block/cross
        Material material
) {
    public static ItemGraphics from(Map<?, ?> data) {
        Map<?, ?> graphics = YmlUtils.getMap(data, "graphics");
        if (graphics == null)
            return new ItemGraphics(null, null, null, null, Material.PAPER);

        Material material = Material.valueOf(
                YmlUtils.getString(data, "material", ItemResource.DEFAULT_MATERIAL).toUpperCase());

        return new ItemGraphics(
                YmlUtils.getString(graphics, "texture", null),
                YmlUtils.getStringMap(graphics, "textures"),
                YmlUtils.getString(graphics, "model", null),
                YmlUtils.getString(graphics, "parent", null),
                material
        );
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