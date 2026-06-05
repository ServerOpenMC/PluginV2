package fr.openmc.core.features.economy.menu;

import fr.openmc.api.menulib.PaginatedMenu;
import fr.openmc.api.menulib.template.ItemMenuTemplate;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemMenuBuilder;
import fr.openmc.api.menulib.utils.StaticSlots;
import fr.openmc.core.features.economy.Transaction;
import fr.openmc.core.features.economy.TransactionsManager;
import fr.openmc.core.utils.cache.CacheOfflinePlayer;
import fr.openmc.core.utils.text.messages.TranslationManager;
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
    public @NotNull Component getName() {
        return TranslationManager.translation(
                "feature.economy.transactions.menu.title",
                Component.text(CacheOfflinePlayer.getOfflinePlayer(target).getName())
        );
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
            items.add(new ItemMenuBuilder(this, transaction.toItemStack(target)));
        }

        return items;
    }

    @Override
    public Map<Integer, ItemMenuBuilder> getButtons() {
        Map<Integer, ItemMenuBuilder> map = new HashMap<>();
        map.put(49, ItemMenuTemplate.BTN_CANCEL.apply(this));
        map.put(48, ItemMenuTemplate.BTN_PREVIOUS_PAGE_ORANGE.apply(this));
        map.put(50, ItemMenuTemplate.BTN_NEXT_PAGE_ORANGE.apply(this));
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