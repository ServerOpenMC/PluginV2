package fr.openmc.core.features.dream.placeholders;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholder;

public class DreamItemNamePlaceholder implements IAPlaceholder {
    private static final String PLACEHOLDER_NAME = "dream_item_name";

    public String name() {
        return PLACEHOLDER_NAME;
    }

    public String resolve(String idItem) {
        if (idItem == null || idItem.isBlank()) {
            return null;
        }

        DreamItem item = DreamItemRegistry.getBootstrapRegistry().get("omc_dream:" + idItem);

        if (item == null || !(item.getMeta() instanceof DreamItemMeta d)) return null;

        return d.getRarity().getLegacyColor() + d.getName();
    }
}
