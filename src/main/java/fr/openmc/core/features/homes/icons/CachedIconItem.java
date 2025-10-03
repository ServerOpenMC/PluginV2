package fr.openmc.core.features.homes.icons;

import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.menu.HomeChangeIconMenu;
import fr.openmc.core.features.homes.models.Home;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CachedIconItem {

    @Getter private final HomeIcon homeIcon;
    private final ItemStack normalItemWithBuilder, selectedItemWithBuilder;
    private final Component displayName, normalLore, selectedLore;

    /**
     * Constructs a CachedIconItem with the specified HomeIcon and base ItemStack.
     *
     * @param homeIcon        The HomeIcon to be cached.
     * @param baseItemStack   The base ItemStack to use for creating the normal and selected items.
     */
    public CachedIconItem(HomeIcon homeIcon, ItemStack baseItemStack) {
        this.homeIcon = homeIcon;
        this.displayName = Component.text("§a" + homeIcon.getVanillaName());
        this.normalLore = Component.text("§7■ §aClique §2gauche §apour changer l'icône");
        this.selectedLore = Component.text("§8[§a✔§8] §7Icône actuelle");

        this.normalItemWithBuilder = createNormalItemWithBuilder(baseItemStack);
        this.selectedItemWithBuilder = createSelectedItemWithBuilder(baseItemStack);
    }

    /**
     * Creates a normal item with the specified base ItemStack and applies the display name and lore.
     *
     * @param baseItem The base ItemStack to clone and modify.
     * @return A new ItemStack with the display name and lore applied.
     */
    private ItemStack createNormalItemWithBuilder(ItemStack baseItem) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            meta.lore(List.of(normalLore));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Creates a selected item with the specified base ItemStack and applies the display name, lore, and enchantments.
     *
     * @param baseItem The base ItemStack to clone and modify.
     * @return A new ItemStack with the display name, lore, and enchantments applied.
     */
    @SuppressWarnings("UnstableApiUsage")
    private ItemStack createSelectedItemWithBuilder(ItemStack baseItem) {
        ItemStack item = baseItem.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            meta.addEnchant(Enchantment.SHARPNESS, 5, true);
            item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.ENCHANTMENTS).build());
            meta.lore(List.of(selectedLore));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Creates an ItemStack for the HomeChangeIconMenu with the appropriate click handler.
     *
     * @param menu   The HomeChangeIconMenu where this item will be used.
     * @param home   The Home associated with this icon.
     * @param player The Player who will interact with this item.
     * @return An ItemStack that can be used in the menu.
     */
    public ItemStack createItemForMenu(HomeChangeIconMenu menu, Home home, Player player) {
        ItemStack baseItem = home.getIcon().equals(homeIcon) ?
                selectedItemWithBuilder : normalItemWithBuilder;

        return new ItemBuilder(menu, baseItem.clone())
                .setOnClick(event -> {
                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                        home.setIcon(homeIcon);
                        MessagesManager.sendMessage(player,
                                Component.text("§aL'icône de votre home §2" + home.getName() + " §aa été changée en §2" + homeIcon.getVanillaName() + "§a !"),
                                Prefix.HOME, MessageType.SUCCESS, true);
                    });
                    player.closeInventory();
                });
    }
}
