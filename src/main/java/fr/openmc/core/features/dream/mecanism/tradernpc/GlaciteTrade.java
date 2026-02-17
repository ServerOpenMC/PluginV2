package fr.openmc.core.features.dream.mecanism.tradernpc;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.registry.enchantments.CustomEnchantmentRegistry;
import lombok.Getter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

@Getter
public enum GlaciteTrade {
    ORB_GLACITE(
            DreamItemRegistry.getByName("glacite_orb"),
            200,
            15,
            Component.text("§bOrbe de Glacite")
    ),
    SOULBOUND_BOOK(
            (DreamItem) CustomEnchantmentRegistry.getCustomEnchantmentByKey(Key.key("omc_dream:soulbound")).getEnchantedBookItem(2),
            150,
            5,
            Component.text("§bLivre d'enchantement : Soulbound II")
    ),
    SOMNIFERE(
            DreamItemRegistry.getByName("somnifere"),
            20,
            0,
            Component.text("§bSomnifère")
    ),
    ETERNAL_CAMPFIRE(
            DreamItemRegistry.getByName("eternal_campfire"),
            0,
            2,
            Component.text("§bFeu de camp éternel")
    ),
    EWENITE(
            DreamItemRegistry.getByName("ewenite"),
            100,
            0,
            Component.text("§bEwenite")
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