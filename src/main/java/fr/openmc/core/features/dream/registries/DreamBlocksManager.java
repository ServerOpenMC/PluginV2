package fr.openmc.core.features.dream.registries;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.OMCRegistry;
import fr.openmc.core.features.dream.DreamDimensionManager;
import fr.openmc.core.features.dream.listeners.registry.DreamBlocksListeners;
import fr.openmc.core.features.dream.mecanism.altar.AltarManager;
import fr.openmc.core.features.dream.mecanism.cloudcastle.BossCloudSpawner;
import fr.openmc.core.features.dream.mecanism.cloudcastle.CloudVault;
import fr.openmc.core.features.dream.models.registry.DreamBlock;
import fr.openmc.core.lifecycle.integration.OMCLogger;
import fr.openmc.core.lifecycle.interfaces.HasListeners;
import fr.openmc.core.lifecycle.listeners.ListenerFactory;
import fr.openmc.core.registry.features.Feature;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DreamBlocksManager extends Feature implements HasListeners {

    private File file;
    private FileConfiguration config;

    private final List<DreamBlock> dreamBlocks = new ArrayList<>();
    private final Map<String, List<DreamBlock>> cacheByType = new HashMap<>();

    public AltarManager ALTAR;

    @Override
    public void init() {
        ConfigurationSerialization.registerClass(DreamBlock.class);
        file = new File(OMCPlugin.getInstance().getDataFolder() + "/data/dream", "registered_blocks.yml");
        load();

        // # Register DreamBlocks
        ALTAR = OMCRegistry.FEATURES.register(new AltarManager());
    }

    @Override
    public Set<ListenerFactory> getListeners() {
        return Set.of(
                DreamBlocksListeners::new,
                CloudVault::new,
                BossCloudSpawner::new
        );
    }

    public void load() {
        if (!file.exists()) {
            OMCLogger.info("[DreamBlocks] Fichier manquant, il sera créé au save().");
        }

        config = YamlConfiguration.loadConfiguration(file);

        if (DreamDimensionManager.DREAM_WORLD == null) {
            OMCLogger.error("[DreamBlocks] Le monde " + DreamDimensionManager.DIMENSION_NAME + " est introuvable !");
            return;
        }

        dreamBlocks.clear();
        if (DreamDimensionManager.DREAM_WORLD.getName().equalsIgnoreCase(DreamDimensionManager.DIMENSION_NAME) && OMCRegistry.DREAM_FEATURES.DREAM_DIMENSION.hasSeedChanged()) {
            config.set("blocks", new ArrayList<>());
            save();
            return;
        }

        if (config.contains("blocks")) {
            for (Object obj : config.getList("blocks")) {
                if (obj instanceof DreamBlock dreamBlock) {
                    dreamBlocks.add(dreamBlock);
                }
            }
        }
    }

    public void save() {
        config.set("blocks", dreamBlocks);

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDreamBlock(String type, Location loc) {
        DreamBlock entry = new DreamBlock(type, loc);
        if (!dreamBlocks.contains(entry)) {
            dreamBlocks.add(entry);
            cacheByType.computeIfAbsent(type.toLowerCase(), k -> new ArrayList<>())
                    .add(entry);
            save();
        }
    }

    public boolean isDreamBlock(Location loc) {
        return dreamBlocks.stream().anyMatch(e -> e.location().equals(loc));
    }

    public boolean isDreamBlock(Location loc, String type) {
        return dreamBlocks.stream().anyMatch(e -> e.location().equals(loc) && e.type().equalsIgnoreCase(type));
    }

    public List<DreamBlock> getDreamBlocks() {
        return new ArrayList<>(dreamBlocks);
    }

    public List<DreamBlock> getDreamBlocksByType(String type) {
        return cacheByType.getOrDefault(type.toLowerCase(), new ArrayList<>());
    }
}