package fr.openmc.core.hooks.itemsadder.sprite;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.riftengine.api.scanner.items.ItemEntry;
import fr.openmc.riftengine.api.utils.IdentifierUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.ObjectContents;

import java.util.Set;

public class SpriteUtils {
    public static ObjectComponent getSprite(CustomItem item) {
        ItemEntry itemEntry = item.getItemEntry();

        if (itemEntry == null) {
            OMCLogger.warn("ItemEntry is null for item " + item.getId());
            return null;
        }

        Key atlasKey = itemEntry.getBehaviour().isBlockBehaviour() ? Key.key("minecraft", "blocks")
                : Key.key("minecraft", "items");

        Set<String> bestResourcesId = itemEntry.getBestResourcesId();
        if (bestResourcesId == null || bestResourcesId.isEmpty()) {
            OMCLogger.warn("BestResourcesId is null for item " + item.getId());
            return null;
        }
        String bestResourceId = bestResourcesId.stream().findFirst().orElseThrow();
        Key resourceKey = Key.key("_" + IdentifierUtils.removeExtensionPath(bestResourceId));

        System.out.println("atlas " + atlasKey.asMinimalString());
        System.out.println("resource " + resourceKey.asMinimalString());

        return Component.object(ObjectContents.sprite(atlasKey, resourceKey));
    }
}
