package fr.openmc.core.features.credits;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.ItemUtils;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditsMenu extends PaginatedMenu {

    public CreditsMenu(Player owner) {
        super(owner);
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.GRAY_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull InventorySize getInventorySize() {
        return InventorySize.LARGEST;
    }

    @Override
    public int getSizeOfItems() {
        return getItems().size();
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getStandardSlots(getInventorySize());
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Credits credit : Credits.values()) {
            List<Component> lore = new ArrayList<>();

            lore.addAll(TranslationManager.translationLore(
                    "feature.credits.menu.lore.developers",
                    Component.text(String.join(", ", credit.getDeveloppers())).color(NamedTextColor.BLUE)
            ));
            if (!credit.getGraphists().isEmpty()) {
                lore.addAll(TranslationManager.translationLore(
                        "feature.credits.menu.lore.graphists",
                        Component.text(String.join(", ", credit.getGraphists())).color(NamedTextColor.GOLD)
                ));
            }
            if (!credit.getBuilders().isEmpty()) {
                lore.addAll(TranslationManager.translationLore(
                        "feature.credits.menu.lore.builders",
                        Component.text(String.join(", ", credit.getBuilders())).color(NamedTextColor.GREEN)
                ));
            }

            ItemMenuBuilder item = new ItemMenuBuilder(this, credit.getIcon(), itemMeta -> {
                itemMeta.displayName(TranslationManager.translation(credit.getFeatureKey())
                        .color(NamedTextColor.YELLOW)
                        .decoration(TextDecoration.ITALIC, false));
                itemMeta.lore(lore);
            }).hide(ItemUtils.getDataComponentType());

            items.add(item);
        }

        return items;
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();

        map.put(48, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_BACK_ORANGE, itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.previous_page"))).setPreviousPageButton());
        map.put(50, new ItemMenuBuilder(this, OMCRegistry.CUSTOM_ITEMS.ICON_NEXT_ORANGE, itemMeta -> itemMeta.displayName(TranslationManager.translation("messages.menus.next_page"))).setNextPageButton());
        map.put(49, new ItemMenuBuilder(this, Material.BARRIER, meta -> {
            meta.displayName(TranslationManager.translation("feature.credits.menu.close"));
        }).setOnClick(e -> getOwner().closeInventory()));

        return map;
    }

    @Override
    public @NotNull Component getName() {
        return TranslationManager.translation("feature.credits.menu.title");
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
