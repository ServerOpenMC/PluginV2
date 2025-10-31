package fr.openmc.core.features.dream.mecanism.cloudcastle;

import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.registries.mobs.Breezy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.spawner.TrialSpawnerConfiguration;

import java.util.Map;

public class BossCloudSpawner implements Listener {
    @SuppressWarnings("UnstableApiUsage")
    public static void replaceBlockWithBossCloudSpawner(Block block) {
        block.setType(Material.TRIAL_SPAWNER);

        if (block.getState() instanceof TrialSpawner spawner) {
            TrialSpawnerConfiguration normal = spawner.getNormalConfiguration();

            normal.setSpawnedEntity(new Breezy().createSnapshot());

            NamespacedKey lootKey = new NamespacedKey("openmc", "cloud_castle/boss_spawner");
            LootTable lootTable = Bukkit.getLootTable(lootKey);

            if (lootTable != null) {
                normal.setPossibleRewards(Map.of(lootTable, 1));
            }
            normal.setSpawnRange(6);
            normal.setBaseSpawnsBeforeCooldown(1.0f);

            spawner.update();
        }
    }

    @EventHandler
    void onEffect(CreatureSpawnEvent event) {
        if (!DreamUtils.isDreamWorld(event.getLocation())) return;
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.POTION_EFFECT)) return;

        event.getEntity().addPotionEffect(new PotionEffect(
                PotionEffectType.WIND_CHARGED,
                Integer.MAX_VALUE,
                1,
                false,
                true
        ));
    }
}
