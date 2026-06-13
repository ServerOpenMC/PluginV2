package fr.openmc.core.features.events.contents.weeklyevents.contents.contest.menu;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.core.features.events.contents.weeklyevents.WeeklyEventsManager;
import fr.openmc.core.features.events.contents.weeklyevents.contents.contest.ContestPhase;
import fr.openmc.core.features.events.contents.weeklyevents.models.WeeklyEventPhase;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreInfoMenu extends Menu {

    public MoreInfoMenu(Player owner) {
        super(owner);
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.events.contest.more_info.title");
    }

    @Override
    public String getTexture() {
        return FontImageWrapper.replaceFontImages("§r§f:offset_-48::contest_menu:");
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGE;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemMenuBuilder> getContent() {
        Map<Integer, ItemMenuBuilder> inventory = new HashMap<>();

        List<Component> lore0 = TranslationManager.translationLore("feature.events.contest.phase.vote.lore");

        List<Component> lore1 = TranslationManager.translationLore("feature.events.contest.phase.trade.lore");

        List<Component> lore2 = TranslationManager.translationLore("feature.events.contest.phase.end.lore");


        WeeklyEventPhase phase = WeeklyEventsManager.getCurrentPhase();

        boolean ench0 = phase == ContestPhase.VOTE_CAMP.getPhase();
        boolean ench1 = phase == ContestPhase.TRADE_PHASE.getPhase();

        inventory.put(11, new ItemMenuBuilder(this, Material.BLUE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.more_info.vote.name"));
            itemMeta.lore(lore0);
            itemMeta.setEnchantmentGlintOverride(ench0);
        }));

        inventory.put(13, new ItemMenuBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.more_info.trade.name"));
            itemMeta.lore(lore1);
            itemMeta.setEnchantmentGlintOverride(ench1);
        }));

        inventory.put(15, new ItemMenuBuilder(this, Material.YELLOW_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.events.contest.more_info.end.name"));
            itemMeta.lore(lore2);
        }));

        inventory.put(35, new ItemMenuBuilder(this, Material.ARROW, true));

        return inventory;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        //empty
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
