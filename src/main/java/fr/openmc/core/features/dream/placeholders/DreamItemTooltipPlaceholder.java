package fr.openmc.core.features.dream.placeholders;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholder;

public class DreamItemTooltipPlaceholder implements IAPlaceholder {
    private static final String PLACEHOLDER_NAME = "dream_rarity";

    public String name() {
        return PLACEHOLDER_NAME;
    }

    public String resolve(String idItem) {
        if (idItem == null || idItem.isBlank()) {
            return null;
        }

        DreamItem item = DreamItemRegistry.getBootstrapRegistry().get("omc_dream:" + idItem);

        if (item == null || !(item.getMeta() instanceof DreamItemMeta d)) return null;

        DreamRarity rarity = d.getRarity();

        if (rarity == null) {
            return null;
        }

        return tooltip(toTooltipKey(rarity));
    }

    private static String toTooltipKey(DreamRarity rarity) {
        return switch (rarity) {
            case COMMON -> "common";
            case RARE -> "rare";
            case EPIC -> "epic";
            case LEGENDARY -> "legendary";
            case ONIRISIME -> "onirique";
        };
    }

    private static String tooltip(String rarity) {
        return "omc_tooltips:tooltip/" + rarity;
    }
}
