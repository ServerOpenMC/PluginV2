package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.economy.EconomyManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.WitchShopManager;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchChoiceMenu;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.PointRewards;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancementMenu extends Menu {

    public AdvancementMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.halloween.festival.menu.advancement");
    }

    @Override
    public String getTexture() {

        int textureIndex = 4;

        PointRewards reward = TerrorHouseManager.currentRewardToReach;
        int advancement = TerrorHouseManager.globalAdvancement;

        if (!reward.equals(PointRewards.NULL) && advancement < reward.getGlobalPointsRequire()) {
            textureIndex = (int) Math.floor(
                    (double) TerrorHouseManager.globalAdvancement / reward.getGlobalPointsRequire() * 5
            ) - 1;
            textureIndex = Math.clamp(textureIndex, 0, 4);
        }

        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_candy_jar_" + textureIndex + ":");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent e) {

    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> content = new HashMap<>();

        content.put(22, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.CANDY_RED, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.halloween.festival.menu.advancement.item_name"));
            itemMeta.lore(List.of(
                    TranslationManager.translation(
                            "feature.events.halloween.festival.menu.advancement.item_lore",
                            Component.text(EconomyManager.getFormattedSimplifiedNumber(TerrorHouseManager.globalAdvancement)),
                            Component.text(WitchShopManager.getHalloweenPointIcon())
                    )
            ));
        }).setOnClick(click -> {
            if (click.isRightClick()) {
                new RewardViewMenu(getOwner()).open();
            }

            if (click.isLeftClick()) {
                new CandyChoiceMenu(getOwner()).open();
            }
        }));

        content.put(49, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setOnClick(event -> {
            new WitchChoiceMenu(getOwner()).open();
        }));

        return content;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
