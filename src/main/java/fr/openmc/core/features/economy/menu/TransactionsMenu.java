package fr.openmc.core.features.economy.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.economy.Transaction;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.registry.items.CustomItemRegistry;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TransactionsMenu extends PaginatedMenu {
    final Player owner;
    final UUID target;

    public TransactionsMenu(Player owner, UUID target) {
        super(owner);
        this.owner = owner;
        this.target = target;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des transactions de " + CacheOfflinePlayer.getOfflinePlayer(target).getName();
    }

    @Override
    public String getTexture() {
        return null;
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
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @Nullable Material getBorderMaterial() {
        return null;
    }

    @Override
    public @NotNull List<Integer> getStaticSlots() {
        return StaticSlots.getBottomSlots(getInventorySize());
    }

    @Override
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Transaction transaction : TransactionsManager.getTransactionsByPlayers(target)) {
            items.add(new ItemBuilder(this, transaction.toItemStack(target)));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemBuilder> getButtons() {
        Map<Integer, ItemBuilder> map = new HashMap<>();
        map.put(49, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_cancel").getBest(), itemMeta -> itemMeta.displayName(Component.text("§7Fermer"))).setCloseButton());
        map.put(48, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_back_orange").getBest(), itemMeta -> itemMeta.displayName(Component.text("§cPage précédente"))).setPreviousPageButton());
        map.put(50, new ItemBuilder(this, CustomItemRegistry.getByName("_iainternal:icon_next_orange").getBest(), itemMeta -> itemMeta.displayName(Component.text("§aPage suivante"))).setNextPageButton());
        return map;
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