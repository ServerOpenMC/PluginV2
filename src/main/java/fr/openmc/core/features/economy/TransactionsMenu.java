package fr.openmc.core.features.economy;

import fr.openmc.api.menulib.Menu;
import fr.openmc.api.menulib.utils.InventorySize;
import fr.openmc.api.menulib.utils.ItemBuilder;
import fr.openmc.core.utils.CacheOfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TransactionsMenu extends Menu {
    Player owner;
    UUID target;

    public TransactionsMenu(Player owner, UUID target) {
        super(owner);
        this.owner = owner;
        this.target = target;
    }

    @Override
    public @NotNull String getName() {
        return "Menu des Transactions de " + CacheOfflinePlayer.getOfflinePlayer(target).getName();
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
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {}

    @Override
    public @NotNull Map<Integer, ItemBuilder> getContent() {
        Map<Integer, ItemBuilder> items = new HashMap<>();
        int iter = 0;
        for (Transaction transaction: TransactionsManager.getTransactionsByPlayers(target, 54)) {
            items.put(iter, new ItemBuilder(this, transaction.toItemStack(target)));
            iter++;
        }

        return items;
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