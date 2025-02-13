package fr.openmc.core.features.homes.menu;

import dev.lone.itemsadder.api.CustomStack;
import dev.xernas.menulib.PaginatedMenu;
import dev.xernas.menulib.utils.ItemBuilder;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.homes.Home;
import fr.openmc.core.features.homes.HomeIcons;
import fr.openmc.core.features.mailboxes.utils.MailboxMenuManager;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeChangeIconMenu extends PaginatedMenu {

    private final Home home;

    public HomeChangeIconMenu(Player owner, Home home) {
        super(owner);
        this.home = home;
    }

    @Override
    public @NotNull String getName() {
        return PlaceholderAPI.setPlaceholders(getOwner(), "§r§f%img_offset_-8%%img_omc_homes_menus_home%");
    }

    @Override
    public @Nullable Material getBorderMaterial() {
        return Material.BLUE_STAINED_GLASS_PANE;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        List<Integer> staticSlots = new ArrayList<>();
        staticSlots.add(45);
        staticSlots.add(48);
        staticSlots.add(49);
        staticSlots.add(50);
        staticSlots.add(53);

        return staticSlots;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < HomeIcons.values().length; i++) {
            HomeIcons homeIcon = HomeIcons.values()[i];
            items.add(new ItemBuilder(this, CustomStack.getInstance(homeIcon.getId()).getItemStack(), itemMeta -> {
                itemMeta.setDisplayName("§a" + homeIcon.getName());
                itemMeta.setLore(List.of(
                        ChatColor.GRAY + "■ §aClique §2gauche §apour changer l'icône"
                ));

                if(home.getIcon().equals(homeIcon)) {
                    itemMeta.addEnchant(Enchantment.SHARPNESS, 5, false);
                    itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                }
            }).setOnClick(event -> {
                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () -> {
                    home.setIcon(homeIcon);
                    MessagesManager.sendMessage(getOwner(), Component.text("§aL'icône de votre home §2"+ home.getName() + " §achangée avec succès !"), Prefix.HOME, MessageType.SUCCESS, true);
                });
                getOwner().closeInventory();
            }));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemStack> getButtons() {
        Map<Integer, ItemStack> map = new HashMap<>();

        map.put(45, new ItemBuilder(this, CustomStack.getInstance("_iainternal:icon_back_orange").getItemStack(), itemMeta -> itemMeta.setDisplayName("§7Retour")).setBackButton());
        map.put(48, new ItemBuilder(this, MailboxMenuManager.previousPageBtn()).setPreviousPageButton());
        map.put(49, new ItemBuilder(this, MailboxMenuManager.cancelBtn()).setCloseButton());
        map.put(50, new ItemBuilder(this, MailboxMenuManager.nextPageBtn()).setNextPageButton());
        map.put(53, new ItemBuilder(this, CustomStack.getInstance("omc_homes:omc_homes_invisible").getItemStack(), itemMeta -> itemMeta.setDisplayName("§7")));

        return map;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}
}
