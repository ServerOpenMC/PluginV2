package fr.openmc.riftengine.api.scanner.items;


import fr.openmc.riftengine.api.scanner.items.entry.ItemBehaviour;
import fr.openmc.riftengine.api.scanner.items.entry.ItemGraphics;
import fr.openmc.riftengine.api.scanner.items.entry.ItemResource;
import fr.openmc.riftengine.api.utils.IdentifierUtils;
import lombok.Getter;
import org.bukkit.Material;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final Function<Path, Set<Path>> resourcePath;

    private final ItemBehaviour behaviour;

    private final Path sourceYml;

    private ItemEntry(
            String namespace,
            String key,
            Integer customModelData,
            Material material,

            // * Méthodes de création d'items (moderne ou legacy)
            ItemResource resource,
            ItemGraphics graphics,
            Function<Path, Set<Path>> resourcePath,

            // tqdq rift engine faire un mapping des behavours (blocks et furnitures)
            ItemBehaviour behaviour,

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

    public static ItemEntry from(Map<Material, Map<String, Integer>> cmdCache, Path sourceYml, String namespace, String key, Map<?, ?> data) {
        ItemResource itemResource = ItemResource.from(data);
        ItemGraphics itemGraphics = ItemGraphics.from(data);
        ItemBehaviour behaviour = ItemBehaviour.from(data);

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

    public Set<String> getBestResourcesId() {
        if (resource != null) {
            if (resource.hasTextures())
                return resource.textures().stream()
                        .map(id -> IdentifierUtils.normalizeId(id, namespace))
                        .collect(Collectors.toSet());
            else if (resource.hasModel())
                return Collections.singleton(IdentifierUtils.normalizeId(resource.model(), namespace));
        }

        // * Méthode graphics
        if (graphics != null) {
            if (graphics.hasModel()) {
                return Collections.singleton(IdentifierUtils.normalizeId(graphics.model(), namespace));
            }
            if (graphics.hasTexture()) {
                return Collections.singleton(IdentifierUtils.normalizeId(graphics.texture(), namespace));
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

    private static Function<Path, Set<Path>> resolvePathFunction(String namespace, ItemGraphics graphics, ItemResource resource) {
        // * Méthode legacy
        if (resource != null) {
            if (resource.hasTextures())
                return javaRoot -> resource.textures().stream()
                        .map(id -> IdentifierUtils.resolveTextureId(javaRoot,
                                IdentifierUtils.normalizeId(id, namespace)))
                        .collect(Collectors.toSet());
            else if (resource.hasModel())
                return javaRoot -> Collections.singleton(IdentifierUtils.resolveModelId(javaRoot,
                        IdentifierUtils.normalizeId(resource.model(), namespace)));
        }

        // * Méthode graphics
        if (graphics != null) {
            if (graphics.hasModel()) {
                return javaRoot -> Collections.singleton(IdentifierUtils.resolveModelId(javaRoot,
                        IdentifierUtils.normalizeId(graphics.model(), namespace)));
            }
            if (graphics.hasTexture()) {
                return javaRoot -> Collections.singleton(IdentifierUtils.resolveTextureId(javaRoot,
                        IdentifierUtils.normalizeId(graphics.texture(), namespace)));
            }
        }

        return _ -> null;
    }

    public String namespacedId() {
        return namespace + ":" + key;
    }
}