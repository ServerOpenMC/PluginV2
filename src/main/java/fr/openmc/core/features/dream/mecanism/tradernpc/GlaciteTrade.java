package fr.openmc.core.features.dream.mecanism.tradernpc;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public enum GlaciteTrade {
    ORB_GLACITE(
            DreamItemRegistry.GLACITE_ORB,
            200,
            15,
            TranslationManager.translation("feature.dream.trader.trade.glacite_orb")
    ),
    SOULBOUND_BOOK(
            (DreamItem) OMCRegistry.CUSTOM_ENCHANTS.SOULBOUND.getEnchantedBookItem(2),
            150,
            5,
            TranslationManager.translation("feature.dream.trader.trade.soulbound_book")
    ),
    SOMNIFERE(
            DreamItemRegistry.SOMNIFERE,
            20,
            0,
            TranslationManager.translation("feature.dream.trader.trade.somnifere")
    ),
    ETERNAL_CAMPFIRE(
            DreamItemRegistry.ETERNAL_CAMPFIRE,
            0,
            2,
            TranslationManager.translation("feature.dream.trader.trade.eternal_campfire")
    ),
    EWENITE(
            DreamItemRegistry.EWENITE,
            80,
            0,
            TranslationManager.translation("feature.dream.trader.trade.ewenite")
    );
    private final DreamItem result;
    private final int glaciteCost;
    private final int eweniteCost;
    private final Component displayName;

    GlaciteTrade(DreamItem result, int glaciteCost, int eweniteCost, Component displayName) {
        this.result = result;
        this.glaciteCost = glaciteCost;
        this.eweniteCost = eweniteCost;
        this.displayName = displayName;
    }
}
