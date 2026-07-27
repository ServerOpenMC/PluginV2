package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.contents.placeholder;

import fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.AbondanceArmorManager;
import fr.openmc.core.hooks.itemsadder.placeholders.IAPlaceholder;

public class DoubleCropsChancePlaceholder implements IAPlaceholder {

    public String name() {
        return "armor_double_crops_chance";
    }

    public String resolve(String idItem) {
        return String.valueOf(AbondanceArmorManager.DOUBLE_CROPS_ARMOR_MODIFIER * 100);
    }
}
