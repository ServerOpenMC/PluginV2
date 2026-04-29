package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamBlocksRegistry;
import fr.openmc.core.features.dream.registries.DreamItemRegistry;
import fr.openmc.core.utils.messages.MessageType;
import fr.openmc.core.utils.messages.MessagesManager;
import fr.openmc.core.utils.messages.Prefix;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class AltarListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location loc = block.getLocation();

        if (!DreamBlocksRegistry.isDreamBlock(loc, "altar")) return;

        event.setCancelled(true);

        if (AltarManager.hasItem(loc)) {
            AltarManager.tryRitual(player, loc);
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType().isAir()) {
            MessagesManager.sendMessage(player,
                    Component.text("Vous devez tenir un objet dans la main"),
                    Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        DreamItem dreamItem = DreamItemRegistry.getByItemStack(handItem);

        if (dreamItem == null) {
            MessagesManager.sendMessage(player,
                    Component.text("Cet objet ne peut pas être utilisé dans l'§5Altar"),
                    Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        AltarManager.bindItem(player, loc, handItem);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        AltarManager.boundPlayers.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(player.getUniqueId())) {
                AltarManager.unbind(entry.getKey());
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        AltarManager.boundPlayers.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(player.getUniqueId())) {
                AltarManager.unbind(entry.getKey());
                return true;
            }
            return false;
        });
    }
}
