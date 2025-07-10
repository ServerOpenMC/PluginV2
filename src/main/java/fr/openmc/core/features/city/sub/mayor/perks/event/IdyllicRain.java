package fr.openmc.core.features.city.sub.mayor.perks.event;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.city.City;
import fr.openmc.core.features.city.CityManager;
import fr.openmc.core.utils.ChunkPos;
import fr.openmc.core.utils.customitems.CustomItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class IdyllicRain implements Listener {

    /**
     * Spawns Aywenite items in the specified city.
     *
     * @param city       The city where the items will be spawned.
     * @param totalItems The total number of items to spawn.
     */
    public static void spawnAywenite(City city, int totalItems) {
        Set<ChunkPos> chunks = city.getChunks();
        if (chunks.isEmpty()) return;

        World world = Bukkit.getWorld("world");
        if (world == null) return;

        List<ChunkPos> chunkList = new ArrayList<>(chunks);
        Random random = new Random();
        NamespacedKey key = new NamespacedKey(OMCPlugin.getInstance(), "city_aywenite");

        final int[] dropped = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (dropped[0] >= totalItems) {
                    this.cancel();
                    return;
                }

                ChunkPos chunk = chunkList.get(random.nextInt(chunkList.size()));
                int chunkX = chunk.getX();
                int chunkZ = chunk.getZ();

                int x = (chunkX << 4) + random.nextInt(16);
                int z = (chunkZ << 4) + random.nextInt(16);
                int y = world.getHighestBlockYAt(x, z) + 10;

                Location dropLoc = new Location(world, x + 0.5, y, z + 0.5);

                ItemStack aywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
                ItemMeta meta = aywenite.getItemMeta();
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, city.getUUID());
                aywenite.setItemMeta(meta);

                Item droppedItem = world.dropItemNaturally(dropLoc, aywenite);
                droppedItem.setGlowing(true);
                droppedItem.setUnlimitedLifetime(false);

                Bukkit.getScheduler().runTaskLater(OMCPlugin.getInstance(), droppedItem::remove, 20L * 60 * 5); // 5 minutes

                dropped[0]++;
            }
        }.runTaskTimer(OMCPlugin.getInstance(), 0L, 12L); // chute d'aywentie tout les 12 ticks
    }


    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack item = event.getItem().getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey key = new NamespacedKey(OMCPlugin.getInstance(), "city_aywenite");
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) return;

        String cityId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        City playerCity = CityManager.getPlayerCity(player.getUniqueId());

        if (playerCity == null || !playerCity.getUUID().equals(cityId)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        ItemStack cleanAywenite = CustomItemRegistry.getByName("omc_items:aywenite").getBest();
        cleanAywenite.setAmount(item.getAmount());

        event.getItem().remove();
        player.getInventory().addItem(cleanAywenite);
    }

}
