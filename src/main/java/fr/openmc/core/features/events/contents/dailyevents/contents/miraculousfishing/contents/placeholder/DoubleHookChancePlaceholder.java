package fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.contents.placeholder;

import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholder;

import static fr.openmc.core.features.events.contents.dailyevents.contents.miraculousfishing.FishingAttributeManager.ARMOR_DOUBLE_HOOK_MODIFIER;

public class DoubleHookChancePlaceholder implements IAPlaceholder {

    public String name() {
        return "armor_double_hook_chance";
    }

    public String resolve(String idItem) {
        return String.valueOf(ARMOR_DOUBLE_HOOK_MODIFIER * 100);
    }
}
