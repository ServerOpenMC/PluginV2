package fr.openmc.core.features.dimsopener;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.dimsopener.event.DimensionUnlockedEvent;
import fr.openmc.core.features.dimsopener.model.DimensionProgress;
import fr.openmc.core.items.CustomItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DimsOpenerManager {

    private static final Map<String, DimensionConfig> dimensionConfigs = new HashMap<>();
    private static final Map<String, DimensionProgress> progressCache = new HashMap<>();
    private static Dao<DimensionProgress, Integer> dao;

    public static void initDB(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTableIfNotExists(connectionSource, DimensionProgress.class);
        dao = DaoManager.createDao(connectionSource, DimensionProgress.class);

        List<DimensionProgress> allProgress = dao.queryForAll();
        allProgress.forEach(progress -> progressCache.put(progress.getDimensionKey(), progress));
    }

    public static void initConfig() {
        File configFile = new File(OMCPlugin.getInstance().getDataFolder(), "data/dimsopener.yml");

        if (!configFile.exists()) {
            OMCPlugin.getInstance().saveResource("data/dimsopener.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection dimsSection = config.getConfigurationSection("dims");

        if (dimsSection == null) return;

        for (String dimKey : dimsSection.getKeys(false)) {
            ConfigurationSection dimSection = dimsSection.getConfigurationSection(dimKey);
            if (dimSection == null) continue;

            List<?> stepsList = dimSection.getList("steps");
            if (stepsList == null) continue;

            List<DimensionStep> steps = new ArrayList<>();

            for (Object stepObj : stepsList) {
                if (!(stepObj instanceof Map<?, ?> stepMap)) continue;

                System.out.println("Processing step map: " + stepMap);

                for (Map.Entry<?, ?> entry : stepMap.entrySet()) {
                    String itemType = entry.getKey().toString();
                    if (!(entry.getValue() instanceof Map<?, ?> itemData)) continue;

                    int requiredAmount = itemData.containsKey("count") ? ((Number) itemData.get("count")).intValue() : 0;
                    int nextStepDelay = itemData.containsKey("next_step_delay") ? ((Number) itemData.get("next_step_delay")).intValue() : 0;

                    if (requiredAmount == 0 && nextStepDelay == 0) continue;

                    steps.add(new DimensionStep(itemType, requiredAmount, nextStepDelay));
                }
            }

            String dimensionName = dimSection.getString("name", dimKey);
            String dimensionIconStr = dimSection.getString("icon", "minecraft:grass_block");
            ItemStack dimensionIcon;

            System.out.println(dimensionIconStr.split(":")[0]);

            if (dimensionIconStr.split(":")[0].equals("minecraft")) {
                dimensionIcon = new ItemStack(Material.matchMaterial(dimensionIconStr.split(":")[1]) != null ? Material.matchMaterial(dimensionIconStr.split(":")[1]) : Material.GRASS_BLOCK);
            } else {
                System.out.println("Loading custom item for dimension icon: " + dimensionIconStr);
                dimensionIcon = CustomItemRegistry.getByName(dimensionIconStr).getBest();
            }

            System.out.println("Loaded dimension: " + dimKey + " with name: " + dimensionName + " and icon: " + dimensionIcon);

            dimensionConfigs.put(dimKey, new DimensionConfig(dimKey, dimensionName, dimensionIcon, steps));

            if (!progressCache.containsKey(dimKey)) {
                DimensionProgress progress = new DimensionProgress(dimKey);
                progressCache.put(dimKey, progress);
            }
        }
    }

    public static DimensionProgress getProgress(String dimensionKey) {
        return progressCache.get(dimensionKey);
    }

    public static DimensionConfig getConfig(String dimensionKey) {
        return dimensionConfigs.get(dimensionKey);
    }

    public static boolean contributeItems(String dimensionKey, ItemStack item) {
        DimensionProgress progress = progressCache.get(dimensionKey);
        DimensionConfig config = dimensionConfigs.get(dimensionKey);


        if (progress == null || config == null || progress.isUnlocked() || !progress.canProgressToNextStep()) return false;

        List<DimensionStep> steps = config.steps();
        int currentStepIndex = progress.getCurrentStep();

        if (currentStepIndex >= steps.size()) {
            return false;
        }

        DimensionStep currentStep = steps.get(currentStepIndex);
        if (!item.getType().toString().equals(currentStep.itemType())) return false;

        int toAdd = Math.min(item.getAmount(), currentStep.count() - progress.getItemsCollected());
        item.setAmount(item.getAmount() - toAdd);

        if (progress.getItemsCollected() >= currentStep.count())
            completeCurrentStep(progress, currentStep);

        return true;
    }

    private static void completeCurrentStep(DimensionProgress progress, DimensionStep currentStep) {
        progress.setItemsCollected(0);
        progress.setCurrentStep(progress.getCurrentStep() + 1);

        DimensionConfig config = dimensionConfigs.get(currentStep.itemType());
        if (config == null) return;

        if (progress.getCurrentStep() >= config.steps().size()) {
            progress.setUnlocked(true);
            progress.setNextStepUnlockTime(0);

            Bukkit.getPluginManager().callEvent(
                    new DimensionUnlockedEvent(progress.getDimensionKey())
            );
        } else {
            long unlockTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(currentStep.nextStepDelay());
            progress.setNextStepUnlockTime(unlockTime);
        }
    }

    private static void save() {
        try {
            for (DimensionProgress progress : progressCache.values()) {
                dao.createOrUpdate(progress);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFormattedTimeRemaining(String dimensionKey) {
        DimensionProgress progress = progressCache.get(dimensionKey);
        if (progress == null || progress.isUnlocked() || progress.canProgressToNextStep()) {
            return "Disponible maintenant";
        }

        long remain = progress.getCurrentStep() - System.currentTimeMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(remain);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remain) % 60;

        return String.format("Disponible dans %dh %dm", hours, minutes);
    }

    public static Map<String, DimensionConfig> getAllConfigs() {
        return Collections.unmodifiableMap(dimensionConfigs);
    }
}
