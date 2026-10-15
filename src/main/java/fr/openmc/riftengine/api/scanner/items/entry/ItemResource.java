package fr.openmc.riftengine.api.scanner.items.entry;

import fr.openmc.core.utils.YmlUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public record ItemResource(
        Material material,
        boolean generate,
        List<String> textures,
        String model
) {
    public static final String DEFAULT_MATERIAL = "PAPER";

    public static ItemResource from(Map<?, ?> data) {
        Object resourceObj = data.get("resource");
        if (!(resourceObj instanceof Map<?, ?> resource))
            return new ItemResource(Material.valueOf(DEFAULT_MATERIAL), false, List.of(), null);

        Material material = Material.valueOf(YmlUtils.getString(
                resource.get("material"), DEFAULT_MATERIAL).toUpperCase());

        boolean generate = YmlUtils.getBool(resource.get("generate"), false);

        List<String> textures = resource.get("textures") instanceof List<?> list && resource.get("textures") != null
                ? list.stream().map(String::valueOf).toList()
                : List.of();

        if (resource.get("textures") == null && resource.get("texture") != null && textures.isEmpty())
            textures = List.of(String.valueOf(resource.get("texture")));

        if (textures.isEmpty() && resource.get("textures") instanceof String string)
            textures = List.of(string);

        String modelPath = resource.get("model_path") != null
                ? String.valueOf(resource.get("model_path"))
                : null;

        return new ItemResource(material, generate, textures, modelPath);
    }

    public boolean hasTextures() {
        if (textures == null) return false;
        return !textures.isEmpty();
    }

    public boolean hasModel() {
        if (model == null) return false;
        return !model.isEmpty();
    }
}