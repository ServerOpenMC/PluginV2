package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/*
 * Permet d'implémenter des DreamItem via un datapack.
 */
public class DreamItemConvertorListener implements Listener {
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Item item = event.getItem();
        ItemStack converted = tryConvertDreamItem(item.getItemStack());
        item.setItemStack(converted != null ? converted : item.getItemStack());
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        Item item = event.getItem();
        ItemStack converted = tryConvertDreamItem(item.getItemStack());
        item.setItemStack(converted != null ? converted : item.getItemStack());
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        List<ItemStack> newLoot = new ArrayList<>();

        for (ItemStack item : event.getLoot()) {
            ItemStack converted = tryConvertDreamItem(item);
            newLoot.add(converted != null ? converted : item);
        }

        event.setLoot(newLoot);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (!DreamUtils.isDreamWorld(event.getEntity().getWorld())) return;

        Item item = event.getEntity();
        ItemStack converted = tryConvertDreamItem(item.getItemStack());
        item.setItemStack(converted != null ? converted : item.getItemStack());
    }

    private ItemStack tryConvertDreamItem(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey("openmc", "to_dream_item");
        if (!container.has(key, PersistentDataType.STRING)) return null;

        String dreamId = container.get(key, PersistentDataType.STRING);
        DreamItem dreamItem = DreamItemRegistry.getByName(dreamId);
        if (dreamItem == null) return null;

        ItemStack newItem = dreamItem.getBest();
        newItem.setAmount(stack.getAmount());
        return newItem;
    }
}
