package fr.openmc.core.features.dream.blocks;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dream.blocks.altar.AltarManager;
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

public class DreamBlocksManager {

    private static File file;
    private static FileConfiguration config;

    private static final List<DreamBlock> dreamBlocks = new ArrayList<>();

    public static void init() {
        OMCPlugin.registerEvents(
                new DreamBlocksListeners()
        );
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "registered_blocks.yml");
        load();

        // # Register DreamBlocks
        AltarManager.init();
    }

    public static void load() {
        if (!file.exists()) {
            OMCPlugin.getInstance().getSLF4JLogger().info("[DreamBlocks] Fichier manquant, il sera créé au save().");
        }

        config = YamlConfiguration.loadConfiguration(file);

        World dream = Bukkit.getWorld(DreamDimensionManager.DIMENSION_NAME);
        if (dream == null) {
            OMCPlugin.getInstance().getSLF4JLogger().error("[DreamBlocks] Le monde " + DreamDimensionManager.DIMENSION_NAME + " est introuvable !");
            return;
        }

        if (dream.getName().equalsIgnoreCase(DreamDimensionManager.DIMENSION_NAME) && DreamDimensionManager.hasSeedChanged()) {
            dreamBlocks.clear();
            config.set("blocks", new ArrayList<>());
            save();
            return;
        }

        dreamBlocks.clear();
        if (config.contains("blocks")) {
            for (Object obj : config.getList("blocks")) {
                if (!(obj instanceof String s)) continue;
                DreamBlock entry = DreamBlock.fromString(s);
                if (entry != null) dreamBlocks.add(entry);
            }
        }
    }

    public static void save() {
        List<String> serialized = new ArrayList<>();
        for (DreamBlock entry : dreamBlocks) {
            serialized.add(entry.toString());
        }
        config.set("blocks", serialized);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addDreamBlock(String type, Location loc) {
        DreamBlock entry = new DreamBlock(type, loc);
        if (!dreamBlocks.contains(entry)) {
            dreamBlocks.add(entry);
            save();
        }
    }

    public static void removeDreamBlock(Location loc) {
        dreamBlocks.removeIf(e -> e.location().equals(loc));
        save();
    }

    public static boolean isDreamBlock(Location loc) {
        return dreamBlocks.stream().anyMatch(e -> e.location().equals(loc));
    }

    public static boolean isDreamBlock(Location loc, String type) {
        return dreamBlocks.stream().anyMatch(e -> e.location().equals(loc) && e.type().equalsIgnoreCase(type));
    }

    public static List<DreamBlock> getDreamBlocks() {
        return new ArrayList<>(dreamBlocks);
    }

    public static List<DreamBlock> getDreamBlocksByType(String type) {
        return dreamBlocks.stream().filter(e -> e.type().equalsIgnoreCase(type)).toList();
    }
}