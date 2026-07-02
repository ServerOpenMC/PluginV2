package fr.openmc.core.features.dream.models.registry.items;

import fr.openmc.core.utils.text.ColorUtils;
import fr.openmc.core.utils.text.messages.TranslationManager;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public enum DreamRarity {
    COMMON(TranslationManager.translation("feature.dream.item.rarity.common"), NamedTextColor.WHITE),
    RARE(TranslationManager.translation("feature.dream.item.rarity.rare"), NamedTextColor.BLUE),
    EPIC(TranslationManager.translation("feature.dream.item.rarity.epic"), NamedTextColor.DARK_PURPLE),
    LEGENDARY(TranslationManager.translation("feature.dream.item.rarity.legendary"), NamedTextColor.GOLD),
    ONIRISIME(TranslationManager.translation("feature.dream.item.rarity.onirisme"), NamedTextColor.AQUA);

    private final Component templateLore;
    private final NamedTextColor color;

    DreamRarity(Component templateLore, NamedTextColor color) {
        this.templateLore = templateLore;
        this.color = color;
    }

    public String toLegacyColor() {
        return ColorUtils.getColorCode(color);
    }
}
