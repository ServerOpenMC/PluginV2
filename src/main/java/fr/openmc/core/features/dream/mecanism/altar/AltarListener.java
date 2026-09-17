package fr.openmc.core.features.dream.mecanism.altar;

import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.registries.DreamBlocksManager;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
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

    private final DreamBlocksManager dreamBlocksManager;
    private final AltarManager altarManager;

    public AltarListener() {
        this.dreamBlocksManager = OMCRegistry.DREAM_FEATURES.DREAM_BLOCKS;
        this.altarManager = dreamBlocksManager.ALTAR;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!event.getAction().isRightClick()) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location loc = block.getLocation();

        if (!dreamBlocksManager.isDreamBlock(loc, "altar")) return;

        event.setCancelled(true);

        if (altarManager.hasItem(loc)) {
            altarManager.tryRitual(player, loc);
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType().isAir()) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.dream.altar.message.must_hold_item"),
                    Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        DreamItem dreamItem = OMCRegistry.DREAM_ITEM.getByItemStack(handItem);

        if (dreamItem == null) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.dream.altar.message.unusable_item"),
                    Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        altarManager.bindItem(player, loc, handItem);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        altarManager.boundPlayers.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(player.getUniqueId())) {
                altarManager.unbind(entry.getKey());
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        altarManager.boundPlayers.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(player.getUniqueId())) {
                altarManager.unbind(entry.getKey());
                return true;
            }
            return false;
        });
    }
}
