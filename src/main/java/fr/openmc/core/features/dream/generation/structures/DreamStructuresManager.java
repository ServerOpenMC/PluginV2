package fr.openmc.core.features.dream.generation.structures;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DreamStructuresManager {

    private static File file;
    private static FileConfiguration config;

    private static final List<DreamStructure> structures = new ArrayList<>();

    public static void init() {
        ConfigurationSerialization.registerClass(DreamStructure.class);
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "structures.yml");
        load();
    }

    public static void load() {
        if (!file.exists()) {
            OMCPlugin.getInstance().getSLF4JLogger().info("[DreamStructures] Fichier manquant, il sera créé au save().");
        }

        config = YamlConfiguration.loadConfiguration(file);

        World dream = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        if (dream == null) {
            OMCPlugin.getInstance().getSLF4JLogger().warn("[DreamStructures] Le monde world_dream est introuvable !");
            return;
        }

        structures.clear();
        if (DreamDimensionManager.hasSeedChanged()) {
            config.set("structures", new ArrayList<>());
            save();
            OMCPlugin.getInstance().getSLF4JLogger().info("[DreamStructures] Seed changée, reset du fichier structures.yml !");
            return;
        }

        if (config.contains("structures")) {
            for (Object obj : config.getList("structures")) {
                if (obj instanceof DreamStructure ds) {
                    structures.add(ds);
                }
            }
        }

        OMCPlugin.getInstance().getSLF4JLogger().info("[DreamStructures] Chargé {} structures.", structures.size());
    }

    public static void save() {
        config.set("structures", structures);

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addStructure(DreamStructure.DreamType type, BlockVector3 min, BlockVector3 max) {
        DreamStructure entry = new DreamStructure(type, min, max);
        if (!structures.contains(entry)) {
            structures.add(entry);
            save();
        }
    }

    public static DreamStructure getStructureAt(Location loc) {
        for (DreamStructure s : structures) {
            if (s.isInside(loc)) return s;
        }

        return null;
    }

    public static boolean isInsideStructure(Location loc, DreamStructure.DreamType type) {
        DreamStructure structure = getStructureAt(loc);

        if (structure == null) return false;

        return structure.type().equals(type);
    }
}
