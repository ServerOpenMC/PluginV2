package fr.openmc.core.features.dream.listeners.dream;

import fr.openmc.core.features.dream.DreamDimensionManager;
import org.bukkit.GameRules;
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
            world.setGameRule(GameRules.ADVANCE_TIME, false);
            world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
            world.setGameRule(GameRules.ADVANCE_WEATHER, false);
            world.setGameRule(GameRules.RAIDS, true);
            world.setGameRule(GameRules.SPAWN_PATROLS, false);
            world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
            world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);
            world.setGameRule(GameRules.LOCATOR_BAR, false);
            world.setGameRule(GameRules.ALLOW_ENTERING_NETHER_USING_PORTALS, false);

            // ** SET WORLD BORDER AND TIME **
            world.getWorldBorder().setSize(10000);
            world.setTime(18000);
        }
    }
}