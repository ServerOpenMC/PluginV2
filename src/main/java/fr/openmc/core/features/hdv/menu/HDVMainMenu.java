package fr.openmc.core.features.hdv.menu;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.MenuLib;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.features.hdv.HDVListing;
import fr.openmc.core.features.hdv.HDVModule;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HDVMainMenu extends Menu {
    private final HDVModule hdvModule;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HDVMainMenu(Player player) {
        super(player);
        this.hdvModule = HDVModule.getInstance();
    }

    @Override
    @NotNull
    public String getName() {
        return "§6Hôtel des Ventes";
    }

    @Override
    public String getTexture() {
        return "";
    }

    @Override
    @NotNull
    public InventorySize getInventorySize() {
        return InventorySize.LARGEST; // 54 slots
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        // handled by ItemBuilder click handlers (registered in getContent)
        event.setCancelled(true);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        // no-op
    }

    @Override
    @NotNull
    public Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> content = new HashMap<>();
        List<HDVListing> listings = hdvModule.getAllListings();
        int slot = 0;
        int max = getInventorySize().getSize() - 1; // reserve last slot for help

        for (HDVListing listing : listings) {
            if (slot >= max) break;
            ItemStack displayItem = listing.getItem().clone();

            ItemBuilder builder = new ItemBuilder(this, displayItem, itemMeta -> {
                // build lore using Components
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(""));
                lore.add(Component.text("§e▶ Prix: §f" + listing.getPrice() + " §ecoins"));
                Player seller = Bukkit.getPlayer(listing.getSeller());
                lore.add(Component.text("§e▶ Vendeur: §f" + (seller != null ? seller.getName() : "Inconnu")));
                lore.add(Component.text("§e▶ Mis en vente le: §f" + listing.getCreatedAt().format(DATE_FORMAT)));
                lore.add(Component.text(""));
                lore.add(Component.text("§a► Cliquez pour acheter"));
                // using Paper API to set component lore/display name if supported
                try {
                    var meta = displayItem.getItemMeta();
                    if (meta != null) {
                        // Paper's ItemMeta component API
                        try {
                            meta.displayName(displayItem.hasItemMeta() && displayItem.getItemMeta().hasDisplayName()
                                    ? displayItem.getItemMeta().displayName()
                                    : Component.text(displayItem.getType().name()));
                            // set lore if supported
                            meta.lore(lore);
                            displayItem.setItemMeta(meta);
                        } catch (NoSuchMethodError ignored) {
                            // fallback: do nothing
                        }
                    }
                } catch (Throwable ignored) {
                    // ignore fallback
                }
            });

            // register click handler
            builder.setOnClick(ev -> {
                Player p = (Player) ev.getWhoClicked();
                hdvModule.buyItem(p, listing);
                // reopen menu to refresh content
                MenuLib.pushMenu(p, new HDVMainMenu(p));
            });

            content.put(slot, builder);
            slot++;
        }

        // help button at last slot
        ItemBuilder help = new ItemBuilder(this, new ItemStack(Material.BOOK), meta -> {
            try {
                meta.displayName(Component.text("§e§lAide HDV"));
                List<Component> helpLore = new ArrayList<>();
                helpLore.add(Component.text("§7Pour vendre un objet:"));
                helpLore.add(Component.text("§f1. §7Prenez l'objet en main"));
                helpLore.add(Component.text("§f2. §7Tapez §e/hdv sell <prix>"));
                helpLore.add(Component.text(""));
                helpLore.add(Component.text("§7Pour acheter:"));
                helpLore.add(Component.text("§f▶ §7Cliquez sur l'objet souhaité"));
                meta.lore(helpLore);
            } catch (NoSuchMethodError ignored) {
            }
        });
        help.setCloseButton();
        content.put(getInventorySize().getSize() - 1, help);

        return content;
    }

    @Override
    public List<Integer> getTakableSlot() {
        return List.of();
    }
}
