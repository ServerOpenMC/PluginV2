package fr.openmc.core.features.events.contents.dailyevents.contents.goldenharvest.obesecrops;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.bootstrap.integration.OMCLogger;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ObeseCropsRegistry {
    private static File file;
    private static FileConfiguration config;

    private static final Set<ObeseCropBlock> obeseBlocks = ConcurrentHashMap.newKeySet();

    public static void init() {
        ConfigurationSerialization.registerClass(ObeseCropBlock.class);
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data/", "obesecrops_blocks.yml");
        load();
    }

    public static void load() {
        if (!file.exists()) {
            OMCLogger.info("[ObeseCrops] Fichier manquant, il sera créé au save().");
        }

        config = YamlConfiguration.loadConfiguration(file);

        obeseBlocks.clear();
        if (config.contains("blocks")) {
            for (Object obj : config.getList("blocks")) {
                if (obj instanceof ObeseCropBlock obeseCropBlock) {
                    obeseBlocks.add(obeseCropBlock);
                }
            }
        }
    }

    public static void save() {
        config.set("blocks", new ArrayList<>(obeseBlocks));

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void mark(Location loc) {
        ObeseCropBlock entry = new ObeseCropBlock(loc.toBlockLocation());
        if (obeseBlocks.add(entry)) {
            save();
        }
    }

    public static void unmark(Location loc) {
        ObeseCropBlock entry = new ObeseCropBlock(loc.toBlockLocation());
        if (obeseBlocks.remove(entry)) {
            save();
        }
    }

    public static boolean isObeseCrop(Location loc) {
        return obeseBlocks.contains(new ObeseCropBlock(loc.toBlockLocation()));
    }
}
