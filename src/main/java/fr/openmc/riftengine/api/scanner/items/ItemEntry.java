package fr.openmc.riftengine.api.scanner.items;

import fr.openmc.riftengine.api.scanner.items.entry.ItemGraphics;
import fr.openmc.riftengine.api.scanner.items.entry.ItemInfo;
import fr.openmc.riftengine.api.scanner.items.entry.ItemResource;
import fr.openmc.riftengine.api.scanner.items.entry.behaviours.IABehaviour;
import fr.openmc.riftengine.api.utils.IdentifierUtils;
import lombok.Getter;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class ItemEntry {
    private final String namespace;
    private final String key;
    private final Integer customModelData;
    private final Material material;

    private final ItemResource resource;
    private final ItemGraphics graphics;
    private final Function<Path, Map<String, Path>> resourcePath;

    private final IABehaviour behaviour;

    private final Path sourceYml;

    private ItemEntry(
            String namespace,
            String key,
            Integer customModelData,
            Material material,

            // * Méthodes de création d'items (moderne ou legacy)
            ItemResource resource,
            ItemGraphics graphics,
            Function<Path, Map<String, Path>> resourcePath,

            IABehaviour behaviour,

            Path sourceYml
    ) {
        this.namespace = namespace;
        this.key = key;
        this.customModelData = customModelData;
        this.material = material;
        this.resource = resource;
        this.graphics = graphics;
        this.resourcePath = resourcePath;
        this.behaviour = behaviour;
        this.sourceYml = sourceYml;
    }

    public static ItemEntry from(Map<Material, Map<String, Integer>> cmdCache,
                                 Map<String, String> blockStateCache,
                                 Path sourceYml,
                                 String namespace,
                                 String key,
                                 Map<?, ?> data) {
        ItemInfo itemInfo = ItemInfo.from(namespace, key, cmdCache, blockStateCache);

        ItemResource itemResource = ItemResource.from(data);
        ItemGraphics itemGraphics = ItemGraphics.from(data);
        IABehaviour behaviour = IABehaviour.from(itemInfo, data);

        Material material = getMaterial(itemResource, itemGraphics);

        Integer customModelData = null;

        Map<String, Integer> map = cmdCache.get(material);
        if (map != null && map.get(namespace + ":" + key) != null)
            customModelData = map.get(namespace + ":" + key);

        return new ItemEntry(
                namespace,
                key,
                customModelData,
                material,
                itemResource,
                itemGraphics,
                resolvePathFunction(namespace, itemGraphics, itemResource),
                behaviour,
                sourceYml
        );
    }

    public Map<String, String> getBestResourcesId() {
        if (resource != null) {
            if (resource.hasTextures())
                return resource.textures().stream()
                        .map(id -> IdentifierUtils.normalizeId(id, namespace))
                        .collect(Collectors.toMap(id -> "_", id -> id));
            else if (resource.hasModel())
                return getMapResource(IdentifierUtils.normalizeId(resource.model(), namespace));
        }

        // * Méthode graphics
        if (graphics != null) {
            if (graphics.hasModel()) {
                return getMapResource(IdentifierUtils.normalizeId(graphics.model(), namespace));
            }
            if (graphics.hasTexture()) {
                return getMapResource(IdentifierUtils.normalizeId(graphics.texture(), namespace));
            }
            if (graphics.hasTextures()) {
                return new HashMap<>(graphics.textures());
            }
        }

        return null;
    }

    private static Material getMaterial(ItemResource resource, ItemGraphics graphics) {
        Material byDefault = Material.valueOf(ItemResource.DEFAULT_MATERIAL);
        Material resourceMaterial = resource.material();
        Material graphicsMaterial = graphics.material();

        if (resourceMaterial != byDefault)
            return resourceMaterial;
        if (graphicsMaterial != byDefault)
            return graphicsMaterial;

        return byDefault;
    }

    private static Function<Path, Map<String, Path>> resolvePathFunction(String namespace, ItemGraphics graphics, ItemResource resource) {
        // * Méthode legacy
        if (resource != null) {
            if (resource.hasTextures())
                return javaRoot -> resource.textures().stream()
                        .map(id -> IdentifierUtils.resolveTextureId(javaRoot,
                                IdentifierUtils.normalizeId(id, namespace)))
                        .collect(Collectors.toMap(v -> "_", k -> k));
            else if (resource.hasModel())
                return javaRoot -> getMapResource(IdentifierUtils.resolveModelId(javaRoot,
                        IdentifierUtils.normalizeId(resource.model(), namespace)));
        }

        // * Méthode graphics
        if (graphics != null) {
            if (graphics.hasModel()) {
                return javaRoot -> getMapResource(IdentifierUtils.resolveModelId(javaRoot,
                        IdentifierUtils.normalizeId(graphics.model(), namespace)));
            }
            if (graphics.hasTexture()) {
                return javaRoot -> getMapResource(IdentifierUtils.resolveTextureId(javaRoot,
                        IdentifierUtils.normalizeId(graphics.texture(), namespace)));
            }
            if (graphics.hasTextures()) {
                return javaRoot -> graphics.textures().entrySet().stream()
                        .map(entry -> Map.entry(entry.getKey(), IdentifierUtils.resolveTextureId(javaRoot,
                                IdentifierUtils.normalizeId(entry.getValue(), namespace))))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }
        }

        return _ -> null;
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }

    public static <T> Map<String, T> getMapResource(T value) {
        return Map.of("_", value);
    }
}