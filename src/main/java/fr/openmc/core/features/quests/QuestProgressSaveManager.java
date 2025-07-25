package fr.openmc.core.features.quests;

import fr.openmc.core.OMCPlugin;
import fr.openmc.core.features.quests.objects.Quest;
import fr.openmc.core.features.quests.objects.QuestStep;
import fr.openmc.core.features.quests.objects.QuestTier;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Manages the saving and loading of quest progress for players.
 */
public class QuestProgressSaveManager {

    private static final String SAVE_FOLDER = "quests";
    static final Map<UUID, Map<String, Object>> playerQuestProgress = new ConcurrentHashMap<>();

    /**
     * Constructor for QuestProgressSaveManager.
     */
    public QuestProgressSaveManager() {
        File saveFolder = new File(OMCPlugin.getInstance().getDataFolder(), SAVE_FOLDER);
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }
    }

    /**
     * Loads the quest progress for a specific player.
     * @param playerUUID the UUID of the player
     */
    public static void loadPlayerQuestProgress(UUID playerUUID) {
        File playerFile = getPlayerProgressFile(playerUUID);
        if (playerFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            Map<String, Object> playerProgress = new HashMap<>();

            for (Quest quest : QuestsManager.quests.values()) {
                String questName = quest.getName();

                int progress = config.getInt(questName + ".progress", 0);
                quest.getProgress().put(playerUUID, progress);

                int currentTier = config.getInt(questName + ".currentTier", 0);
                quest.getCurrentTier().put(playerUUID, currentTier);

                List<Integer> completedTiers = config.getIntegerList(questName + ".completedTiers");
                quest.getCompletedTiers().put(playerUUID, new HashSet<>(completedTiers));

                playerProgress.put(questName + ".progress", progress);
                playerProgress.put(questName + ".currentTier", currentTier);
                playerProgress.put(questName + ".completedTiers", completedTiers);

                List<Integer> pendingRewardTiers = config.getIntegerList(questName + ".pendingRewards");
                for (int tierIndex : pendingRewardTiers) {
                    QuestTier tier = quest.getTiers().get(tierIndex);
                    quest.getPendingRewards()
                            .computeIfAbsent(playerUUID, k -> new HashMap<>())
                            .put(tierIndex, new ArrayList<>(tier.getRewards()));
                }

                for (int tierIndex = 0; tierIndex < quest.getTiers().size(); tierIndex++) {
                    QuestTier tier = quest.getTiers().get(tierIndex);

                    for (int stepIndex = 0; stepIndex < tier.getSteps().size(); stepIndex++) {
                        QuestStep step = tier.getSteps().get(stepIndex);
                        String stepPath = questName + ".tiers." + tierIndex + ".steps." + stepIndex;
                        int stepProgress = config.getInt(stepPath, 0);

                        step.getProgress().put(playerUUID, stepProgress);
                        playerProgress.put(stepPath, stepProgress);
                    }
                }
            }

            playerQuestProgress.put(playerUUID, playerProgress);
        }
    }

    /**
     * Saves the quest progress for a specific player.
     * @param playerUUID the UUID of the player
     */
    public static void savePlayerQuestProgress(UUID playerUUID) {
        File playerFile = getPlayerProgressFile(playerUUID);
        YamlConfiguration config = new YamlConfiguration();

        for (Quest quest : QuestsManager.quests.values()) {
            String questName = quest.getName();
            int progress = quest.getProgress().getOrDefault(playerUUID, 0);
            int currentTier = quest.getCurrentTierIndex(playerUUID);
            Set<Integer> completedTiers = quest.getCompletedTiers().getOrDefault(playerUUID, new HashSet<>());

            config.set(questName + ".progress", progress);
            config.set(questName + ".currentTier", currentTier);
            config.set(questName + ".completedTiers", new ArrayList<>(completedTiers));

            Set<Integer> pendingRewardTier = quest.getPendingRewardTiers(playerUUID);
            config.set(questName + ".pendingRewards", new ArrayList<>(pendingRewardTier));

            for (int tierIndex = 0; tierIndex < quest.getTiers().size(); tierIndex++) {
                QuestTier tier = quest.getTiers().get(tierIndex);

                for (int stepIndex = 0; stepIndex < tier.getSteps().size(); stepIndex++) {
                    QuestStep step = tier.getSteps().get(stepIndex);
                    String stepPath = questName + ".tiers." + tierIndex + ".steps." + stepIndex;

                    int stepProgress = step.getProgress().getOrDefault(playerUUID, 0);
                    config.set(stepPath, stepProgress);
                }
            }
        }

        try {
            config.save(playerFile);
        } catch (IOException e) {
            OMCPlugin.getInstance().getLogger().log(Level.SEVERE, "Could not save quest progress for player " + playerUUID, e);
        }
    }

    /**
     * Saves the quest progress for all players currently online.
     */
    public static void saveAllQuestProgress() {
        OMCPlugin.getInstance().getServer().getOnlinePlayers().forEach((player) ->
                savePlayerQuestProgress(player.getUniqueId())
        );
    }

    /**
     * Deletes the quest progress file for a specific player.
     * @param playerUUID the UUID of the player
     */
    private static File getPlayerProgressFile(UUID playerUUID) {
        return new File(OMCPlugin.getInstance().getDataFolder(), SAVE_FOLDER + File.separator + playerUUID + ".yml");
    }

    /**
     * Loads the quest progress for all players.
     */
    public static void loadAllQuestProgress() {
        File saveFolder = new File(OMCPlugin.getInstance().getDataFolder(), SAVE_FOLDER);
        File[] playerFiles = saveFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (playerFiles != null) {
            for (File playerFile : playerFiles) {
                try {
                    UUID playerUUID = UUID.fromString(playerFile.getName().replace(".yml", ""));
                    loadPlayerQuestProgress(playerUUID);
                } catch (IllegalArgumentException e) {
                    OMCPlugin.getInstance().getLogger().warning("Invalid player file: " + playerFile.getName());
                }
            }
        }
    }
}
