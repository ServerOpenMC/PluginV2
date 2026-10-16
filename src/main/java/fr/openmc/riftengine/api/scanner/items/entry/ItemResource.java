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

        Material material = Material.matchMaterial(YmlUtils.getString(resource, "material", DEFAULT_MATERIAL).toUpperCase());

        return new ItemResource(material,
                YmlUtils.getBool(resource, "generate", false),
                getTextures(resource),
                YmlUtils.getString(resource, "model_path", null)
        );
    }

    private static List<String> getTextures(Map<?, ?> resource) {
        if (resource.get("textures") instanceof List<?>)
            return YmlUtils.getStringList(resource, "textures");

        String single = YmlUtils.getString(resource, "textures",
                YmlUtils.getString(resource, "texture", null));

        return single == null ? List.of() : List.of(single);
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