package fr.openmc.core.features.dream.listeners.registry;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class DreamItemDropsListener implements Listener {

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (DreamItemRegistry.getByItemStack(item) instanceof DreamItem dreamItem && dreamItem.getRarity().equals(DreamRarity.ONIRISIME)) {
            event.setCancelled(true);
            MessagesManager.sendMessage(event.getPlayer(), Component.text("Vous ne pouvez pas jeter cet objet !", NamedTextColor.RED), Prefix.DREAM, MessageType.WARNING, true);
        }
    }
}