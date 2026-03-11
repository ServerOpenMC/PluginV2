package fr.openmc.core.features.dream.dimension.listeners;

import fr.openmc.core.features.dream.dimension.DreamDimensionManager;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class SetupDreamDimensionListener implements Listener {
    @EventHandler
    public void onWorldLoad(WorldInitEvent event) {
        World world = event.getWorld();
        if (!world.getName().equals(DreamDimensionManager.DIMENSION_NAME)) return;

        DreamDimensionManager.checkSeed();

        if (DreamDimensionManager.hasSeedChanged()) {
            // ** SPAWNING RULES **
            world.setSpawnLimit(SpawnCategory.MONSTER, 10);
            world.setSpawnLimit(SpawnCategory.AMBIENT, 10);
            world.setSpawnLimit(SpawnCategory.ANIMAL, 6);

            world.setTicksPerSpawns(SpawnCategory.MONSTER, 30);
            world.setTicksPerSpawns(SpawnCategory.AMBIENT, 15);
            world.setTicksPerSpawns(SpawnCategory.ANIMAL, 30);

            // ** SET GAMERULE FOR THE WORLD **
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, true);
            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            world.setGameRule(GameRule.LOCATOR_BAR, false);
            world.setGameRule(GameRule.ALLOW_ENTERING_NETHER_USING_PORTALS, false);

            // ** SET WORLD BORDER AND TIME **
            world.getWorldBorder().setSize(10000);
            world.setTime(18000);
        }
    }
}
