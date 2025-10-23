package fr.openmc.core.features.dream.blocks.cloudspawner;

import fr.openmc.core.features.dream.mobs.mobs.DreamStray;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.loot.LootTable;
import org.bukkit.spawner.TrialSpawnerConfiguration;

import java.util.Map;

public class StrayCloudSpawner implements Listener {
    @SuppressWarnings("UnstableApiUsage")
    public static void replaceBlockWithMobCloudSpawner(Block block) {
        block.setType(Material.TRIAL_SPAWNER);

        if (block.getState() instanceof TrialSpawner spawner) {
            TrialSpawnerConfiguration normal = spawner.getNormalConfiguration();

            normal.setSpawnedEntity(new DreamStray().createSnapshot());

            NamespacedKey lootKey = new NamespacedKey("openmc", "cloud_castle/mob_spawner");
            LootTable lootTable = Bukkit.getLootTable(lootKey);

            if (lootTable != null) {
                normal.setPossibleRewards(Map.of(lootTable, 1));
            }
            normal.setSpawnRange(4);
            normal.setBaseSpawnsBeforeCooldown(4.0f);
            normal.setBaseSimultaneousEntities(2.0f);
            normal.setAdditionalSpawnsBeforeCooldown(1.0f);
            normal.setAdditionalSimultaneousEntities(1.0f);

            spawner.update();
        }
    }
}
