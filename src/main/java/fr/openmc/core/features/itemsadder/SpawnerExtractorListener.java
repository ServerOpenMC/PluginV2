package fr.openmc.core.features.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.utils.EnumUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnerExtractorListener implements Listener {
    private static final double FAILURE_CHANCE = 0.4; // 40% chance d’échec
    private static final NamespacedKey KEY_SPAWNER_MOB = new NamespacedKey(OMCPlugin.getInstance(), "spawner_extractor_mob_spawner");

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER)
            return;

        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        CustomStack custom = CustomStack.byItemStack(tool);
        if (custom == null || !custom.getNamespacedID().equals("omc_items:spawner_extractor"))
            return;

        EntityType entityType = ((CreatureSpawner) block.getState()).getSpawnedType();
        if (entityType == null)
            return;

        if (ThreadLocalRandom.current().nextDouble() < FAILURE_CHANCE) {
            player.sendActionBar(Component.text("L'extraction a échoué ! Le spawner s'est brisé.", NamedTextColor.RED));
            return;
        }

        block.setType(Material.AIR);
        event.setDropItems(false);
        event.setExpToDrop(0);

        ItemStack spawnerItem = ItemStack.of(Material.SPAWNER);
        spawnerItem.editMeta(meta -> {
            meta.displayName(Component.text("Spawner à ", NamedTextColor.YELLOW)
                    .append(Component.translatable(entityType.translationKey())));

            meta.getPersistentDataContainer().set(KEY_SPAWNER_MOB, PersistentDataType.STRING, entityType.name());
        });

        block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
        player.sendActionBar(Component.text("Vous avez extrait un spawner à ", NamedTextColor.GREEN)
                .append(Component.translatable(entityType.translationKey()))
                .append(Component.text(" !")));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.SPAWNER)
            return;

        String mobName = item.getItemMeta().getPersistentDataContainer().get(KEY_SPAWNER_MOB, PersistentDataType.STRING);
        if (mobName == null)
            return;

        EntityType entityType = EntityType.fromName(mobName);
        if (entityType == null)
            return;

        Block block = event.getBlockPlaced();
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        spawner.setSpawnedType(entityType);
        spawner.update();

        event.getPlayer().sendActionBar(Component.text("Spawner à ", NamedTextColor.GREEN)
                .append(Component.translatable(entityType.translationKey()))
                .append(Component.text(" placé !")));
    }
}