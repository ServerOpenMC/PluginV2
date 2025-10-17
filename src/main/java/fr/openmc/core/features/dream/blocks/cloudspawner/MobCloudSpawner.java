package fr.openmc.core.features.dream.blocks.cloudspawner;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.spawner.TrialSpawnerConfiguration;

public class MobCloudSpawner implements Listener {
    @SuppressWarnings("UnstableApiUsage")
    public static void replaceBlockWithMobCloudSpawner(Block block) {
        block.setType(Material.TRIAL_SPAWNER);

        if (block.getState() instanceof TrialSpawner spawner) {
            TrialSpawnerConfiguration normal = spawner.getNormalConfiguration();

            normal.setSpawnedType(EntityType.PHANTOM);

            //todo: add loot table
            normal.setSpawnRange(6);
            normal.setBaseSpawnsBeforeCooldown(3.0f);
            normal.setBaseSimultaneousEntities(2.0f);
            normal.setAdditionalSpawnsBeforeCooldown(1.0f);
            normal.setAdditionalSimultaneousEntities(1.0f);

            spawner.update();
        }
    }
}
