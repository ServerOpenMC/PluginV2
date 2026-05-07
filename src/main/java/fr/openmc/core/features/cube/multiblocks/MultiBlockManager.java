package fr.openmc.core.features.cube.multiblocks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.features.Feature;
import fr.openmc.core.bootstrap.features.types.HasCommands;
import fr.openmc.core.bootstrap.features.types.HasListeners;
import fr.openmc.core.bootstrap.features.types.LoadAfterItemsAdder;
import fr.openmc.core.bootstrap.features.types.NotInUnitTest;
import fr.openmc.core.features.cube.Cube;
import fr.openmc.core.features.cube.CubeCommands;
import fr.openmc.core.features.cube.listeners.CubeListener;
import fr.openmc.core.features.cube.listeners.RepulseEffectListener;
import fr.openmc.core.features.dream.DreamUtils;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MultiBlockManager extends Feature implements LoadAfterItemsAdder, NotInUnitTest, HasListeners, HasCommands {
    private static final OMCPlugin plugin = OMCPlugin.getInstance();
    @Getter
    public static final List<MultiBlock> multiBlocks = new ArrayList<>();
    private static FileConfiguration config = null;
    private static File file = null;

    @Override
    public void init() {
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "multiblocks.yml");
        if (!file.exists()) {
            plugin.saveResource("data/multiblocks.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        load();
    }

    @Override
    public Set<Object> getCommands() {
        return Set.of(
                new CubeCommands()
        );
    }

    @Override
    public Set<Listener> getListeners() {
        return Set.of(
                new CubeListener(),
                new MultiBlocksListeners(),
                new RepulseEffectListener()
        );
    }

    public static void load() {
        multiBlocks.clear();

        List<Map<?, ?>> list = config.getMapList("multiblocks");
        for (Map<?, ?> map : list) {
            String type = (String) map.get("type");
            String worldName = (String) map.get("world");
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getSLF4JLogger().warn("World '{}' not found for multiblock '{}', skipping...", worldName, type);
                continue;
            }

            Map<?, ?> origin = (Map<?, ?>) map.get("origin");
            int x = (int) origin.get("x");
            int z = (int) origin.get("z");

            int y;
            if (DreamUtils.isDreamWorld(world) && DreamDimensionManager.hasSeedChanged()) {
                plugin.getSLF4JLogger().warn("Changing y pos for '{}' because Dream Dimension seed changed", type);
                y = world.getHighestBlockYAt(x, z) + 1;
            } else {
                y = origin.containsKey("y") ? (int) origin.get("y") : world.getHighestBlockYAt(x, z) + 1;
            }

            int size = (int) map.get("size");
            String matName = (String) map.get("material");
            Material material = Material.valueOf(matName);
            boolean vulnerable = (boolean) map.get("vulnerable");
            boolean bossbar = (boolean) map.get("bossbar");

            Location loc = new Location(world, x, y, z);

            if ("CUBE".equalsIgnoreCase(type)) {
                Cube cube = new Cube(loc, size, material, bossbar);
                cube.setVulnerable(vulnerable);
                cube.build();
                multiBlocks.add(cube);
            }
        }
    }

    @Override
    public void save() {
        saveConfig();
    }

    public static void saveConfig() {
        if (config == null) return;

        List<Map<String, Object>> list = new ArrayList<>();
        for (MultiBlock mb : multiBlocks) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", mb.getClass().getSimpleName().replace("MultiBlock", "").toUpperCase());
            map.put("world", mb.origin.getWorld().getName());

            Map<String, Object> origin = new HashMap<>();
            origin.put("x", mb.origin.getBlockX());
            origin.put("y", mb.origin.getBlockY());
            origin.put("z", mb.origin.getBlockZ());
            map.put("origin", origin);

            map.put("size", mb.radius);
            map.put("material", mb.material.name());
            map.put("vulnerable", mb.vulnerable);

            if (mb instanceof Cube cube)
                map.put("bossbar", cube.showBossBar);

            list.add(map);
        }

        config.set("multiblocks", list);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getSLF4JLogger().error("Could not save multiblocks.yml", e);
        }
    }

    public static void register(MultiBlock multiBlock) {
        multiBlocks.add(multiBlock);

        saveConfig();
    }
    
    public static @Nullable MultiBlock getMultiblockAtDimension(String worldName) {
        for (MultiBlock multiBlock : multiBlocks) {
            if (multiBlock.origin.getWorld().getName().equals(worldName)) {
                return multiBlock;
            }
        }
        return null;
    }
}
