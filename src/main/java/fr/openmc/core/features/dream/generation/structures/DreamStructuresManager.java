package fr.openmc.core.features.dream.generation.structures;

import com.sk89q.worldedit.math.BlockVector3;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.generation.DreamDimensionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DreamStructuresManager {

    private static File file;
    private static FileConfiguration config;

    private static final List<DreamStructure> structures = new ArrayList<>();

    public static void init() {
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "structures.yml");
        load();
    }

    public static void load() {
        if (!file.exists()) {
            OMCPlugin.getInstance().getLogger().info("[DreamStructures] Fichier manquant, il sera créé au save().");
        }

        config = YamlConfiguration.loadConfiguration(file);

        World dream = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        if (dream == null) {
            OMCPlugin.getInstance().getLogger().warning("[DreamStructures] Le monde world_dream est introuvable !");
            return;
        }

        if (DreamDimensionManager.hasSeedChanged()) {
            structures.clear();
            config.set("structures", new ArrayList<>());
            save();
            OMCPlugin.getInstance().getLogger().info("[DreamStructures] Seed changée, reset du fichier structures.yml !");
            return;
        }

        structures.clear();
        if (config.contains("structures")) {
            for (Object obj : config.getList("structures")) {
                if (!(obj instanceof String s)) continue;
                DreamStructure structure = DreamStructure.fromString(s);
                if (structure != null) structures.add(structure);
            }
        }

        OMCPlugin.getInstance().getLogger().info("[DreamStructures] Chargé " + structures.size() + " structures.");
    }

    public static void save() {
        List<String> serialized = new ArrayList<>();
        for (DreamStructure structure : structures) {
            serialized.add(structure.toString());
        }
        config.set("structures", serialized);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
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
