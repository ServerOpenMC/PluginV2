package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.events.contents.halloween.festival.shop.menu.WitchShopMainMenu;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.TerrorHouseManager;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.advancement.reward.PointReward;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardViewMenu extends Menu {

    public RewardViewMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.adminshop.menu.confirm.name");//TODO
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::halloween_reward_menu:");
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

        PointReward reward = TerrorHouseManager.currentRewardToReach.getReward();

        content.put(13, new ItemMenuBuilder(this, reward.getItemMenuPreview(), itemMeta -> {
            itemMeta.displayName(reward.getRewardName());
            itemMeta.lore(reward.getRewardLore());
        }));

        content.put(49, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_CANCEL, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("messages.menus.close"));
        }).setOnClick(event -> {
            new AdvancementMenu(getOwner()).open();
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
