package fr.openmc.core.features.events.contents.christmas.mecanism.gift.models;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.registry.items.CustomItem;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum GiftType {
    COMMON("omc_christmas:white_gift",
            "itemsadder.omc_chirstmas.white_gift.name"),
    RARE("omc_christmas:green_gift",
            "itemsadder.omc_chirstmas.green_gift.name"),
    LEGENDARY("omc_christmas:yellow_gift",
            "itemsadder.omc_chirstmas.yellow_gift.name"),
    ONIRISME("omc_christmas:blue_gift",
            "itemsadder.omc_chirstmas.blue_gift.name")
    ;

    private final String id;
    private final String translationKey;

    GiftType(String id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public CustomItem getGiftItem() {
        return OMCRegistry.CUSTOM_ITEMS.get(id).orElse(null);
    }

    public CustomLootTable getGiftTable() {
        return OMCRegistry.CUSTOM_LOOT_TABLES.get(id).orElse(null);
    }

    public Component getTranslatableName() {
        return TranslationManager.translation(translationKey);
    }
}
