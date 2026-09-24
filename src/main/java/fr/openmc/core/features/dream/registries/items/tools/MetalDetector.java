package fr.openmc.core.features.dream.registries.items.tools;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.events.MetalDetectorLootEvent;
import fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorManager;
import fr.openmc.core.features.dream.mecanism.metaldetector.MetalDetectorTask;
import fr.openmc.core.features.dream.mecanism.rng.DreamRngLootEvent;
import fr.openmc.core.features.dream.models.registry.items.DreamItem;
import fr.openmc.core.features.dream.models.registry.items.DreamItemMeta;
import fr.openmc.core.features.dream.models.registry.items.DreamRarity;
import fr.openmc.core.registry.items.options.UsableItem;
import fr.openmc.core.registry.loottable.CustomLootTable;
import fr.openmc.core.registry.loottable.loots.CustomLoot;
import fr.openmc.core.utils.text.messages.MessageType;
import fr.openmc.core.utils.text.messages.MessagesManager;
import fr.openmc.core.utils.text.messages.Prefix;
import fr.openmc.core.utils.text.messages.TranslationManager;
import fr.openmc.core.utils.world.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class MetalDetector extends DreamItem implements UsableItem {
    private MetalDetectorManager manager;

    public MetalDetector() {
        super(new DreamItemMeta(
                "omc_dream:metal_detector",
                TranslationManager.translation("feature.dream.item.metal_detector.name"),
                DreamRarity.EPIC,
                Material.PAPER,
                false
        ));
    }

    @Override
    public ItemStack getTransferableItem() {
        return null;
    }

    @Override
    public void onRightClick(Player player, PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        if (!DreamUtils.isDreamWorld(event.getClickedBlock().getLocation())) return;
        UUID uuid = player.getUniqueId();
        if (!getCachedManager().hiddenChests.containsKey(uuid)) return;

        if (clicked.getType() == Material.CHEST) {
            event.setCancelled(true);
            MetalDetectorTask task = getCachedManager().hiddenChests.remove(uuid);
            task.cancel();
            Location chestLoc = task.getChestLocation();

            if (LocationUtils.isSameLocation(clicked.getLocation(), chestLoc)) {
                event.setCancelled(true);
                clicked.setType(Material.MUD);
                CustomLootTable lootTable = OMCRegistry.DREAM_LOOT_TABLE.METAL_DETECTOR;
                if (lootTable == null) return;

                List<CustomLoot> rewards = lootTable.rollLoots(player).loots();

                for (CustomLoot loot : rewards) {
                    if (!(loot instanceof ItemStack item)) continue;

                    Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                            Bukkit.getServer().getPluginManager().callEvent(new DreamRngLootEvent(player, item, item.getAmount(), lootTable.getChanceOf(item)))
                    );
                }

                Bukkit.getScheduler().runTask(OMCPlugin.getInstance(), () ->
                        Bukkit.getServer().getPluginManager().callEvent(new MetalDetectorLootEvent(player, rewards))
                );
            }
        }
    }

    @Override
    public void onSneakClick(Player player, PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        World world = player.getWorld();
        if (!DreamUtils.isDreamWorld(world)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.item.metal_detector.message.must_be_dream"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        UUID playerUUID = player.getUniqueId();

        if (!getCachedManager().hiddenChests.containsKey(playerUUID)) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        if (player.hasCooldown(item)) {
            MessagesManager.sendMessage(player, TranslationManager.translation("feature.dream.item.metal_detector.message.reset_cooldown"), Prefix.DREAM, MessageType.ERROR, false);
            return;
        }

        player.setCooldown(item, 15 * 20);

        MetalDetectorTask oldTask = getCachedManager().hiddenChests.get(playerUUID);
        oldTask.getChestLocation().getBlock().setType(Material.MUD);
        Location newLoc = getCachedManager().findRandomChestLocation(player.getLocation());
        MetalDetectorTask newTask = new MetalDetectorTask(player, newLoc);
        newTask.runTaskTimer(OMCPlugin.getInstance(), 0L, 5L);
        getCachedManager().hiddenChests.put(playerUUID, newTask);
        oldTask.cancel();
    }

    private MetalDetectorManager getCachedManager() {
        if (manager == null)
            manager = OMCRegistry.DREAM_FEATURES.METAL_DETECTOR;
        return manager;
    }
}
