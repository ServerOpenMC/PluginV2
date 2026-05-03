package iascript;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Event;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.lone.itemsadder.api.*;
import dev.lone.itemsadder.api.scriptinginternal.*;
import fr.openmc.core.features.city.ProtectionsManager;

public class builderwand extends ItemScript {

    private static final int RADIUS = 2;

    public void handleEvent(Plugin plugin, Event event, Player player, CustomStack $customStack, ItemStack $itemStack) {
        if (!(event instanceof PlayerInteractEvent playerInteractEvent))
            return;
        playerInteractEvent.setCancelled(true);
        Block origin = playerInteractEvent.getClickedBlock();
        if (origin == null)
            return;

        BlockFace face = playerInteractEvent.getBlockFace();
        Material type = origin.getType();
        if (type.isAir())
            return;

        ItemStack wand = player.getInventory().getItemInMainHand();
        CustomStack cs = CustomStack.byItemStack(wand);
        if (cs == null)
            return;

        for (int a = -RADIUS; a <= RADIUS; a++) {
            for (int b = -RADIUS; b <= RADIUS; b++) {

                Block base;
                switch (face) {
                    case UP, DOWN -> base = origin.getRelative(a, 0, b);
                    case NORTH, SOUTH -> base = origin.getRelative(a, b, 0);
                    case EAST, WEST -> base = origin.getRelative(0, b, a);
                    default -> base = null;
                }

                if (base == null)
                    continue;

                Block toPlace = base.getRelative(face);

                if (base.getType() != type)
                    continue;
                if (!toPlace.getType().isAir())
                    continue;
                if (!ProtectionsManager.canInteract(player, toPlace.getLocation()))
                    continue;
                if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && !player.getInventory().contains(type))
                    continue;

                toPlace.setType(type, false);

                if (player.getGameMode() != org.bukkit.GameMode.CREATIVE) {
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null)
                            continue;
                        if (item.getType() != type)
                            continue;

                        item.setAmount(item.getAmount() - 1);
                        break;
                    }
                }
            }
        }
    }
}
