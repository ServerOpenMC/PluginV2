package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.placeholder;

import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholder;

import static fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.FishingAttributeManager.ARMOR_FISHING_SPEED_MODIFIER;

public class FishingSpeedPlaceholder implements IAPlaceholder {
    public String name() {
        return "armor_fishing_speed";
    }

    public String resolve(String idItem) {
        return String.valueOf(ARMOR_FISHING_SPEED_MODIFIER * 100);
    }
}
