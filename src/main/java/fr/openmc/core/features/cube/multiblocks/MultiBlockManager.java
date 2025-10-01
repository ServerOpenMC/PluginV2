package fr.openmc.core.features.cube.multiblocks;

import fr.openmc.core.CommandsManager;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.cube.Cube;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiBlockManager {
    private static final OMCPlugin plugin = OMCPlugin.getInstance();
    @Getter
    public static final List<MultiBlock> multiBlocks = new ArrayList<>();
    private static FileConfiguration config = null;
    private static File file = null;

    public MultiBlockManager() {
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data", "multiblocks.yml");
        if (!file.exists()) {
            plugin.saveResource("data/multiblocks.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        load();
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

            int y = origin.containsKey("y") ? (int) origin.get("y") : world.getHighestBlockYAt(x, z) + 1;

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

    public static void save() {
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

        save();
    }

    public static void initCommandSuggestion() {
        CommandsManager.getHandler().getAutoCompleter().registerSuggestion("cubes", (args, sender, command) -> {
            return MultiBlockManager.getMultiBlocks().stream()
                    .filter(mb -> mb instanceof Cube)
                    .map(mb -> {
                        Location loc = mb.origin;
                        return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
                    })
                    .toList();
        });
    }
}
