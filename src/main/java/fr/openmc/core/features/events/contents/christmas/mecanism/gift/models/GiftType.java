package fr.openmc.core.features.events.contents.christmas.mecanism.gift.models;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.christmas.contents.items.GiftItem;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum GiftType {
    COMMON(OMCRegistry.CUSTOM_ITEMS.COMMON_GIFT, "itemsadder.omc_chirstmas.white_gift.name"),
    RARE(OMCRegistry.CUSTOM_ITEMS.RARE_GIFT, "itemsadder.omc_chirstmas.green_gift.name"),
    LEGENDARY(OMCRegistry.CUSTOM_ITEMS.LEGENDARY_GIFT, "itemsadder.omc_chirstmas.yellow_gift.name"),
    ONIRISME(OMCRegistry.CUSTOM_ITEMS.ONIRISIME_GIFT, "itemsadder.omc_chirstmas.blue_gift.name")

    ;

    private final GiftItem giftItem;
    private final String translationKey;

    // todo: mettre loot table associé,
    GiftType(GiftItem item, String translationKey) {
        this.giftItem = item;
        this.translationKey = translationKey;
    }

    public String getId() {
        return giftItem.getId();
    }

    public Component getTranslatableName() {
        return TranslationManager.translation(translationKey);
    }
}
