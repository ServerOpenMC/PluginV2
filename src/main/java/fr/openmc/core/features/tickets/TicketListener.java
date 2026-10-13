package fr.openmc.core.features.tickets;

import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlacedEvent;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.events.LootboxRewardEvent;
import fr.openmc.core.features.tickets.menus.MachineBallsMenu;
import fr.openmc.core.registry.loottable.loots.ItemLoot;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class TicketListener implements Listener {

    private final TicketManager manager;

    public TicketListener(TicketManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onGetRewardLootBox(LootboxRewardEvent event) {
        if (!event.getBox().getNamespace().equals("omc:machine_ball")) return;

        String pelushKey = "omc_plush:peluche_seinyy";

        if (!(event.getLoot() instanceof ItemLoot itemLoot)) return;

        boolean hasLootPelucheSeinyy = false;
        if (OMCRegistry.CUSTOM_ITEMS.get(itemLoot.getItemLootWithAmount()).isPresent()) {
            hasLootPelucheSeinyy = OMCRegistry.CUSTOM_ITEMS.getOrThrow(itemLoot.getItemLootWithAmount())
                    .getId().equals(pelushKey);
        }

        Player player = event.getPlayer();
        PlayerStats ps = manager.getPlayerStats(player.getUniqueId());
        if (ps == null) return;
        int alreadyWon = ps.getMaxItemsGiven().getOrDefault(pelushKey, 0);

        if (hasLootPelucheSeinyy && alreadyWon >= 1) {
            MessagesManager.sendMessage(player,
                    TranslationManager.translation("feature.tickets.loot.limit_reached")
                            .append(OMCRegistry.CUSTOM_ITEMS.getOrThrow(pelushKey).getBest().displayName().color(NamedTextColor.RED)),
                    Prefix.OPENMC, MessageType.ERROR, true);
            event.setCancelled(true);
            return;
        } else if (hasLootPelucheSeinyy && alreadyWon == 0) {
            ps.getMaxItemsGiven().put(pelushKey, alreadyWon + 1);
        }

        manager.setTicketGiven(player.getUniqueId(), ps.getTicketRemaining(), ps.isTicketGiven());
    }

    @EventHandler
    public void onMachineBallsInteraction(FurnitureInteractEvent furniture) {
        if (Objects.equals(furniture.getNamespacedID(), "omc_blocks:ball_machine")) {
            furniture.getPlayer().playSound(Sound.sound(Key.key("minecraft", "block.barrel.open"), Sound.Source.BLOCK, 1f, 1f));
            new MachineBallsMenu(furniture.getPlayer()).open();
        }
    }

    @EventHandler
    public void onMachinePlaced(FurniturePlacedEvent event) {
        if (Objects.equals(event.getNamespacedID(), "omc_blocks:ball_machine")) {
            Bukkit.getScheduler().runTaskLater(fr.openmc.core.OMCPlugin.getInstance(), () -> {
                Location machineLocation = event.getBukkitEntity().getLocation();
                manager.createMachineHologram(machineLocation);
            }, 1L);
        }
    }

    @EventHandler
    public void onMachineBreak(FurnitureBreakEvent event) {
        if (Objects.equals(event.getNamespacedID(), "omc_blocks:ball_machine")) {
            Location machineLocation = event.getBukkitEntity().getLocation();
            manager.removeMachineHologram(machineLocation);
        }
    }
}
