package fr.openmc.core.hooks.itemsadder.sprite;

import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.riftengine.api.scanner.items.ItemEntry;
import fr.openmc.riftengine.api.utils.IdentifierUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;

import java.util.Set;

public class SpriteUtils {
    public static ObjectComponent getSprite(CustomItem item) {
        ItemEntry itemEntry = item.getItemEntry();

        if (itemEntry == null)  return null;

        Key atlasKey = itemEntry.getBehaviour().isBlockBehaviour() ? Key.key("minecraft", "blocks")
                : Key.key("minecraft", "items");

        Set<String> bestResourcesId = itemEntry.getBestResourcesId();
        if (bestResourcesId == null || bestResourcesId.isEmpty()) return null;

        String bestResourceId = bestResourcesId.stream().findFirst().orElseThrow();

        String prefix = "_";

        if (itemEntry.getBehaviour().isBlockBehaviour()
                && itemEntry.getGraphics() != null
                && itemEntry.getGraphics().hasTextures()) {
            prefix += "b_";
        }

        Key resourceKey = Key.key(prefix + IdentifierUtils.removeExtensionPath(bestResourceId));

        return Component.object(ObjectContents.sprite(atlasKey, resourceKey))
                .color(NamedTextColor.WHITE);
    }
}
