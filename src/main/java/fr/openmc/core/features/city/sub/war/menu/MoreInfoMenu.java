package fr.openmc.core.features.city.sub.war.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.city.sub.war.WarManager;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
        return TranslationManager.translation("feature.city.war.menu.more_info.menu_title");
    }

    @Override
    public String getTexture() {
        return "§r§f:offset_-48::city_template3x9:";
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.NORMAL;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent click) {
        //empty
    }

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> inventory = new HashMap<>();

        List<Component> lore0 = TranslationManager.translationLore(
                "feature.city.war.menu.more_info.prep.lore",
                Component.text(WarManager.TIME_PREPARATION)
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore1 = TranslationManager.translationLore(
                "feature.city.war.menu.more_info.combat.lore",
                Component.text(WarManager.TIME_FIGHT)
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore2 = TranslationManager.translationLore("feature.city.war.menu.more_info.result.lore");

        inventory.put(11, new ItemBuilder(this, Material.ORANGE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation(
                    "feature.city.war.menu.more_info.prep.title",
                    Component.text(WarManager.TIME_PREPARATION).color(NamedTextColor.GOLD)
            ));
            itemMeta.lore(lore0);
        }));

        inventory.put(13, new ItemBuilder(this, Material.RED_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation(
                    "feature.city.war.menu.more_info.combat.title",
                    Component.text(WarManager.TIME_FIGHT).color(NamedTextColor.RED)
            ));
            itemMeta.lore(lore1);
        }));

        inventory.put(15, new ItemBuilder(this, Material.WHITE_STAINED_GLASS_PANE, itemMeta -> {
            itemMeta.displayName(TranslationManager.translation("feature.city.war.menu.more_info.result.title"));
            itemMeta.lore(lore2);
        }));

        inventory.put(18, new ItemBuilder(this, Material.ARROW, itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.back")), true));

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
