package fr.openmc.core.features.dream.placeholders;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholder;
import org.bukkit.Material;

public class DreamItemMaterialPlaceholder implements IAPlaceholder {
    private static final String PLACEHOLDER_NAME = "dream_item_material";
    private static final String MATERIAL_FALLBACK = Material.PAPER.toString();

    public String name() {
        return PLACEHOLDER_NAME;
    }

    public String resolve(String idItem) {
        if (idItem == null || idItem.isBlank()) {
            return null;
        }

        DreamItem item = OMCRegistry.DREAM_ITEM.getBootstrapRegistry().get("omc_dream:" + idItem);
        if (item == null || !(item.getMeta() instanceof DreamItemMeta meta)) return MATERIAL_FALLBACK;

        return meta.getDefaultMaterial().toString();
    }
}
