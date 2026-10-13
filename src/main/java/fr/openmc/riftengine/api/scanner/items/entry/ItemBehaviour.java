package fr.openmc.riftengine.api.scanner.items.entry;

import java.util.Map;

public record ItemBehaviour(boolean isBlockBehaviour) {
    public static ItemBehaviour from(Map<?, ?> data) {
        Object resourceObj = data.get("behaviours");
        if (!(resourceObj instanceof Map<?, ?> behaviour))
            return new ItemBehaviour(false);

        Object blockObj = behaviour.get("block");

        if (blockObj != null)
            return new ItemBehaviour(true);

        Object specificPropertiesObj = data.get("specific_properties");

        if (!(specificPropertiesObj instanceof Map<?, ?> specificProperties))
        return new ItemBehaviour(false);

        Object blockObj2 = specificProperties.get("block");

        if (blockObj2 != null)
            return new ItemBehaviour(true);

        return new ItemBehaviour(false);
    }
}
