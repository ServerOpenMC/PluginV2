package fr.openmc.core.features.events.contents.halloween.festival.witchhouse.chests;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.events.contents.halloween.festival.witchhouse.DimensionGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class LootChestManager {

    private static List<LootChest> chests;

    public LootChestManager() {
        chests = getChests();
        placeChest();
        startCooldown();
    }

    public void startCooldown() {
        Bukkit.getScheduler().runTaskTimer(OMCPlugin.getInstance(), () -> {
            for (LootChest chest : chests) {
                chest.update(5);
            }
        }, 0L,  5L * 20L); // 5 seconds
    }

    private void placeChest() {
        for (LootChest chest : chests) {
            DimensionGenerator.HALLOWEEN_WORLD
                    .getBlockAt(
                            chest.getPos().x(),
                            chest.getPos().y(),
                            chest.getPos().z()
                    ).setType(Material.CHEST);
        }
    }

    public static void openChestAt(Location location, Player player) {
        LootChest chest = getChest(location);

        if (chest == null)
            return;
        chest.use(player);
    }

    private static LootChest getChest(Location location) {

        return chests.stream()
                .filter(lootChest -> lootChest.getPos().equals(BukkitAdapter.asBlockVector(location)))
                .findFirst().orElse(null);
    }

    private List<LootChest> getChests() {
        return List.of(
                new LootChest(15 * 60, BlockVector3.at(-41, 21, -71)),
                new LootChest(15 * 60, BlockVector3.at(-39, 15, -62)),
                new LootChest(15 * 60, BlockVector3.at(-38, 5, -64)),
                new LootChest(15 * 60, BlockVector3.at(-63, 10, -72)),
                new LootChest(15 * 60, BlockVector3.at(-78, 30, -56)),
                new LootChest(15 * 60, BlockVector3.at(-40, 37, -66)),
                new LootChest(15 * 60, BlockVector3.at(-60, 40, -81)),
                new LootChest(15 * 60, BlockVector3.at(-58, -59, -67)),
                new LootChest(15 * 60, BlockVector3.at(-50, -59, -75))
        );
    }

}
